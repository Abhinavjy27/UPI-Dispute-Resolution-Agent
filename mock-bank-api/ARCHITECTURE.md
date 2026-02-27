# Mock Bank API - Architecture Reference

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         Client Applications                              │
│  (Python Dispute Resolution Agent, cURL, Postman, Swagger UI)           │
└──────────────────────────────┬──────────────────────────────────────────┘
                               │
                     HTTP/REST Requests
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
     POST            GET (fetch)      POST         GET (fetch all)
  /transaction    /transaction/{id} /refund          /refunds
        │                      │                      │
┌───────▼─────────────────────▼──────────────────────▼──────────────────┐
│                    API KEY FILTER (x-api-key)                         │
│                    (Security/Authentication)                          │
└───────┬──────────────────────────────────────────────────────────────┬┘
        │                                                              │
   Validated                                                      Validated
        │                                                              │
┌───────▼──────────────────────────────────────────────────────────────▼─┐
│                        CONTROLLER LAYER                                 │
├─────────────────────────┬─────────────────────────┬───────────────────┤
│  TransactionController  │                         │  RefundController │
│  - POST /transaction    │                         │  - POST /refund   │
│  - GET /transaction/{id}│                         │  - GET /refunds   │
└─────────────────────────┴─────────────────────────┴───────────────────┘
        │                                                    │
        │          Service Method Calls                     │
        │                                                    │
┌───────▼─────────────────────────────────────────────────▼────────────────┐
│                        SERVICE LAYER                                      │
├────────────────────────────────────────┬────────────────────────────────┤
│     TransactionService                 │      RefundService             │
│  - createTransaction()                 │  - processRefund()             │
│  - getTransaction()                    │  - getAllRefunds()             │
│  - updateTransactionStatus()           │  - getRefundsByTransaction()   │
└────────────────────────────────────────┴────────────────────────────────┘
        │                                                    │
        │          Repository Method Calls                 │
        │                                                    │
┌───────▼─────────────────────────────────────────────────▼────────────────┐
│                     REPOSITORY LAYER                                      │
├────────────────────────────────────────┬────────────────────────────────┤
│   TransactionRepository                │     RefundRepository           │
│   (JPA Queries)                        │     (JPA Queries)              │
│  - findById()                          │  - findAll()                   │
│  - save()                              │  - save()                      │
│  - delete()                            │  - findByTransactionId()       │
└────────────────────────────────────────┴────────────────────────────────┘
        │                                                    │
        │                SQL Queries                        │
        │                                                    │
┌───────▼──────────────────────────────────────────────────▼────────────────┐
│                          DATABASE LAYER                                    │
│                                                                            │
│  ┌───────────────────────────┐  ┌──────────────────────────────────┐    │
│  │   TRANSACTIONS TABLE      │  │    REFUNDS TABLE                 │    │
│  ├───────────────────────────┤  ├──────────────────────────────────┤    │
│  │ transaction_id (PK)       │  │ refund_id (PK, Auto-Inc)         │    │
│  │ amount                    │  │ transaction_id (FK)              │    │
│  │ status                    │  │ amount                           │    │
│  │ timestamp                 │  │ refund_timestamp                 │    │
│  │ payer_id                  │  │ status                           │    │
│  │ payee_id                  │  │ reason                           │    │
│  │ description               │  │                                  │    │
│  └───────────────────────────┘  └──────────────────────────────────┘    │
│                                                                            │
│           H2 Database Engine (In-Memory for Testing)                      │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## Request/Response Flow

### Transaction Creation Flow

```
CLIENT
  │
  ├─ POST /bank/transaction
  |   Headers: x-api-key: ...
  |   Body: { transaction_id, amount, status, ... }
  │
  ▼
API_KEY_FILTER
  │
  ├─ Validate x-api-key header
  │
  ▼ (Valid)
TRANSACTION_CONTROLLER
  │
  ├─ Receive CreateTransactionRequest
  │
  ▼
TRANSACTION_SERVICE
  │
  ├─ Create Transaction entity
  ├─ Set timestamp
  │
  ▼
TRANSACTION_REPOSITORY
  │
  ├─ Save to database
  │
  ▼
RESPONSE
  │
  └─ 201 CREATED + ApiResponse<TransactionResponse>
```

### Refund Processing Flow

```
CLIENT
  │
  ├─ POST /bank/refund
  |   Headers: x-api-key: ...
  |   Body: { transaction_id, reason }
  │
  ▼
API_KEY_FILTER
  │
  ├─ Validate x-api-key header
  │
  ▼ (Valid)
REFUND_CONTROLLER
  │
  ├─ Receive RefundRequest
  │
  ▼
REFUND_SERVICE
  │
  ├─ Fetch transaction from DB
  │  ├─ If not found → Throw TransactionNotFoundException
  │  └─ If found → Continue
  │
  ├─ Check transaction status
  │  ├─ If FAILED → Throw InvalidRefundException
  │  ├─ If REFUNDED → Throw InvalidRefundException
  │  └─ If SUCCESS → Continue
  │
  ├─ Create Refund entity
  ├─ Save refund to DB
  │
  ▼
TRANSACTION_SERVICE
  │
  ├─ Update transaction status to REFUNDED
  ├─ Save updated transaction
  │
  ▼
ERROR_HANDLER (if any exception)
  │
  └─ Return 400/401/404/500 with error message
     (if no error, continue)

  ▼
RESPONSE
  │
  └─ 201 CREATED + ApiResponse<RefundResponse>
```

---

## Exception Handling Flow

```
ANY_COMPONENT
  │
  ├─ Throws Exception
  │  ├─ TransactionNotFoundException
  │  ├─ InvalidRefundException
  │  ├─ UnauthorizedException
  │  └─ Other RuntimeException
  │
  ▼
GLOBAL_EXCEPTION_HANDLER
  │
  ├─ Catch exception type
  │
  ├─ If TransactionNotFoundException
  │  └─ Return 404 NOT FOUND
  │
  ├─ If InvalidRefundException
  │  └─ Return 400 BAD REQUEST
  │
  ├─ If UnauthorizedException
  │  └─ Return 401 UNAUTHORIZED
  │
  ├─ If RuntimeException
  │  └─ Return 500 INTERNAL SERVER ERROR
  │
  ├─ If Other Exception
  │  └─ Return 500 INTERNAL SERVER ERROR
  │
  ▼
CLIENT
  │
  └─ Receives error response with ApiResponse<null>
```

---

## Transaction Lifecycle

```
┌─────────────────────────────────────────────────────────────┐
│               Transaction Lifecycle States                  │
└─────────────────────────────────────────────────────────────┘

1. CREATION
   │
   └─> Create transaction with status: SUCCESS or FAILED
       └─> Timestamp: auto-generated
       └─> Store in DB

2. VERIFICATION (Python Agent)
   │
   ├─> Call GET /bank/transaction/{id}
   │
   ├─> Check status:
   │   ├─ SUCCESS   ← Eligible for refund
   │   └─ FAILED    ← Cannot refund
   │
   └─> Analyze for dispute resolution

3. REFUND ATTEMPT
   │
   ├─> Call POST /bank/refund
   │
   ├─> Validation:
   │   ├─ If FAILED    → Error: "Refund not allowed"
   │   ├─ If REFUNDED  → Error: "Already refunded"
   │   └─ If SUCCESS   → Continue
   │
   └─> Create refund record

4. REFUND CONFIRMATION
   │
   ├─> Update transaction status: REFUNDED
   │
   └─> Refund stored with:
       ├─ Refund ID (auto-generated)
       ├─ Amount
       ├─ Timestamp
       └─ Reason

5. FINAL STATE
   │
   └─> Transaction Status: REFUNDED
       └─> No more refunds allowed
       └─> Refund history maintained
```

---

## Data Model Relationships

```
TRANSACTIONS (Main Table)
┌─────────────────────────────────────────┐
│ transaction_id (PK)                     │
│ amount          (float)                 │
│ status          (SUCCESS/FAILED/REFUND) │
│ timestamp       (datetime)              │
│ payer_id        (varchar)               │
│ payee_id        (varchar)               │
│ description     (text)                  │
└─────────────────────────────────────────┘
           │
           │ 1-to-Many
           │ Relationship
           │ via transaction_id (FK)
           │
           ▼
REFUNDS (Related Table)
┌─────────────────────────────────────────┐
│ refund_id (PK, Auto-Inc)                │
│ transaction_id (FK) ────────────┐       │
│ amount          (float)         │       │
│ refund_timestamp (datetime)     │       │
│ status          (SUCCESS/FAIL)  │       │
│ reason          (text)          │       │
└─────────────────────────────────────────┘
                    │
                    └─ Links to TRANSACTIONS
```

---

## Component Interaction Diagram

```
┌─────────────────┐
│   Controller    │
│   (HTTP Entry)  │
└────────┬────────┘
         │ Calls
         ▼
┌─────────────────┐
│   Service       │ ◄──── Business Logic
│   (Business)    │       - Validation
└────────┬────────┘       - Transformation
         │ Uses           - Coordination
         ▼
┌─────────────────┐
│  Repository     │ ◄──── Data Access
│  (Data Access)  │       - CRUD
└────────┬────────┘
         │ Queries
         ▼
┌─────────────────┐
│  Entity         │ ◄──── Domain Model
│  (Database)     │       - Transaction
└─────────────────┘       - Refund

Security Layer (Wraps All):
│
├─ API Key Filter (validates x-api-key)
│
└─ Global Exception Handler
```

---

## API Response Structure

```
SUCCESS RESPONSE (201 Created)
┌─────────────────────────────────┐
│ {                               │
│   "success": true,              │
│   "message": "...",             │
│   "data": {                     │
│     "field1": "value1",         │
│     "field2": "value2",         │
│     ...                         │
│   },                            │
│   "timestamp": "2024-02-27..."  │
│ }                               │
└─────────────────────────────────┘

ERROR RESPONSE (4xx/5xx)
┌─────────────────────────────────┐
│ {                               │
│   "success": false,             │
│   "message": "Error message",   │
│   "data": null,                 │
│   "timestamp": "2024-02-27..."  │
│ }                               │
└─────────────────────────────────┘
```

---

## Technology Stack Layers

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│  REST API (HTTP/JSON)               │
│  (TransactionController,            │
│   RefundController)                 │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│      Application Layer              │
│  (Spring Boot Framework)            │
│  Security Filter                    │
│  Exception Handler                  │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│      Business Logic Layer           │
│  Services                           │
│  (TransactionService,               │
│   RefundService)                    │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│      Data Access Layer              │
│  Repositories (JPA)                 │
│  (Spring Data JPA)                  │
└─────────────────────────────────────┘
           │
           ▼
┌─────────────────────────────────────┐
│      Data Layer                     │
│  H2 Database                        │
│  (In-Memory)                        │
└─────────────────────────────────────┘
```

---

## Deployment Architecture

```
┌──────────────────────────────────────┐
│     Client Application               │
│  (Python Dispute Resolution Agent)   │
└────────────┬─────────────────────────┘
             │ HTTP Requests
             │ (x-api-key header)
             │
┌────────────▼──────────────────────────┐
│    Mock Bank API Server               │
│    (Spring Boot Application)           │
│                                       │
│  ├─ Tomcat Embedded Server            │
│  │  (Port 8080)                       │
│  │                                    │
│  ├─ REST Endpoints                    │
│  │  - /bank/transaction               │
│  │  - /bank/refund                    │
│  │  - /bank/refunds                   │
│  │  - /swagger-ui.html                │
│  │  - /h2-console                     │
│  │                                    │
│  └─ Data Layer                        │
│     ├─ H2 Database                    │
│     │  ├─ transactions table          │
│     │  └─ refunds table               │
│     └─ (In-Memory)                    │
└────────────┬──────────────────────────┘
             │ SQL Queries
             │
┌────────────▼──────────────────────────┐
│     H2 Database (RAM)                 │
│   - Automatic Schema Creation         │
│   - Pre-loaded Sample Data            │
│   - Lost on Restart                   │
└───────────────────────────────────────┘

For Production:
- Replace H2 with PostgreSQL/MySQL
- Use persistent storage
- Add connection pooling
- Implement monitoring
```

---

## API Authentication Flow

```
REQUEST
  │
  ├─ Headers: x-api-key: <key>
  │
  ▼
API_KEY_FILTER
  │
  ├─ Extract x-api-key header
  │
  ├─ Check if key exists
  │  ├─ No → 401 UNAUTHORIZED
  │  └─ Yes → Continue
  │
  ├─ Validate key value
  │  ├─ Invalid → 401 UNAUTHORIZED
  │  └─ Valid → Continue
  │
  └─ Allow request to proceed
       │
       ▼
   CONTROLLER
       │
       └─ Process request normally
```

---

## Error Handling Hierarchy

```
Exception Thrown
       │
       ▼
Global Exception Handler
       │
       ├─ If TransactionNotFoundException
       │  └─ Return 404 with message
       │
       ├─ If InvalidRefundException
       │  └─ Return 400 with message
       │
       ├─ If UnauthorizedException
       │  └─ Return 401 with message
       │
       ├─ If RuntimeException
       │  └─ Return 500 with message
       │
       └─ If Other Exception
          └─ Return 500 with generic message

All responses wrapped in ApiResponse DTO:
{
  "success": false,
  "message": "...",
  "data": null,
  "timestamp": "..."
}
```

---

## Summary

This architecture ensures:

✅ **Separation of Concerns** - Each layer has specific responsibility  
✅ **Security** - API key validation at filter level  
✅ **Error Handling** - Centralized exception management  
✅ **Scalability** - Layered design allows easy expansion  
✅ **Maintainability** - Clear structure and organization  
✅ **Testing** - Each layer can be tested independently  
✅ **Documentation** - REST API fully documented with Swagger  

The system is designed for:
- **Immediate Use** - With H2 in-memory database
- **Production Ready** - Can switch to PostgreSQL/MySQL
- **Python Integration** - Clear API contracts for agents
- **Extensibility** - Easy to add new features
