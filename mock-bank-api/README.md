# Mock Bank API - Project Documentation

## Overview

The Mock Bank API is a production-like Spring Boot application that simulates real bank infrastructure for the UPI Dispute Resolution Agent. It exposes REST endpoints to manage transactions and refunds.

## Project Structure

```
mock-bank-api/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/mockbank/
│       │       ├── MockBankApiApplication.java          # Main Spring Boot Application
│       │       ├── config/
│       │       │   ├── SwaggerConfig.java                # OpenAPI/Swagger Configuration
│       │       │   └── DataInitializer.java              # Sample Data Initialization
│       │       ├── controller/
│       │       │   ├── TransactionController.java        # Transaction REST Endpoints
│       │       │   └── RefundController.java             # Refund REST Endpoints
│       │       ├── service/
│       │       │   ├── TransactionService.java           # Transaction Business Logic
│       │       │   └── RefundService.java                # Refund Business Logic
│       │       ├── repository/
│       │       │   ├── TransactionRepository.java        # Transaction Data Access
│       │       │   └── RefundRepository.java             # Refund Data Access
│       │       ├── entity/
│       │       │   ├── Transaction.java                  # Transaction Entity
│       │       │   └── Refund.java                       # Refund Entity
│       │       ├── dto/
│       │       │   ├── CreateTransactionRequest.java     # Transaction Request DTO
│       │       │   ├── TransactionResponse.java          # Transaction Response DTO
│       │       │   ├── RefundRequest.java                # Refund Request DTO
│       │       │   ├── RefundResponse.java               # Refund Response DTO
│       │       │   └── ApiResponse.java                  # Generic API Response Wrapper
│       │       ├── exception/
│       │       │   ├── GlobalExceptionHandler.java       # Global Exception Handling
│       │       │   ├── TransactionNotFoundException.java  # Transaction Not Found Exception
│       │       │   ├── InvalidRefundException.java       # Invalid Refund Exception
│       │       │   └── UnauthorizedException.java        # API Key Auth Exception
│       │       └── security/
│       │           └── ApiKeyFilter.java                 # API Key Authentication Filter
│       └── resources/
│           └── application.yml                           # Application Configuration
├── pom.xml                                               # Maven POM File
└── README.md                                             # This File
```

## Technology Stack

- **Java Version**: 17+
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven
- **Database**: H2 (In-Memory)
- **ORM**: Hibernate/Spring Data JPA
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Additional Libraries**:
  - Lombok (Reducing Boilerplate)
  - Jackson (JSON Processing)
  - Apache Commons

## Layered Architecture

### 1. **Controller Layer** (`com.mockbank.controller`)
- Handles HTTP requests and responses
- Maps REST endpoints
- Validates input parameters
- Uses Swagger annotations for API documentation

### 2. **Service Layer** (`com.mockbank.service`)
- Contains all business logic
- Orchestrates operations between controllers and repositories
- Handles transaction and refund processing
- Manages data transformations

### 3. **Repository Layer** (`com.mockbank.repository`)
- Database access layer
- Extends JpaRepository for CRUD operations
- Provides custom query methods

### 4. **Entity Layer** (`com.mockbank.entity`)
- JPA entities mapped to database tables
- Represents Transaction and Refund data models

### 5. **DTO Layer** (`com.mockbank.dto`)
- Data Transfer Objects for API requests/responses
- Decouples API contract from entity models
- Uses Lombok for getter/setter generation

### 6. **Exception Layer** (`com.mockbank.exception`)
- Custom exceptions for domain-specific errors
- Global exception handler for consistent error responses

### 7. **Security Layer** (`com.mockbank.security`)
- API key authentication filter
- Validates x-api-key header on all /bank endpoints

## REST API Endpoints

### Base URL
```
http://localhost:8080
```

### 1. Create Transaction
**Endpoint**: `POST /bank/transaction`

**Request Headers**:
- `x-api-key`: upi-dispute-resolver-secret-key-2024 (required)
- `Content-Type`: application/json

**Request Body**:
```json
{
  "transaction_id": "TXN20240101001",
  "amount": 1000.50,
  "status": "SUCCESS",
  "payer_id": "CUST001",
  "payee_id": "MERCHANT001",
  "description": "Payment for services"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Transaction created successfully",
  "data": {
    "transaction_id": "TXN20240101001",
    "amount": 1000.50,
    "status": "SUCCESS",
    "timestamp": "2024-02-27T10:30:45.123456",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT001",
    "description": "Payment for services"
  },
  "timestamp": "2024-02-27T10:30:45.123456"
}
```

---

### 2. Fetch Transaction Details
**Endpoint**: `GET /bank/transaction/{transactionId}`

**Request Headers**:
- `x-api-key`: upi-dispute-resolver-secret-key-2024 (required)

**Path Parameter**:
- `transactionId`: The transaction ID to fetch (example: TXN20240101001)

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Transaction retrieved successfully",
  "data": {
    "transaction_id": "TXN20240101001",
    "amount": 1000.50,
    "status": "SUCCESS",
    "timestamp": "2024-02-27T10:30:45.123456",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT001",
    "description": "Payment for services"
  },
  "timestamp": "2024-02-27T10:30:45.123456"
}
```

**Error Response** (404 Not Found):
```json
{
  "success": false,
  "message": "Transaction not found with ID: TXN20240101099",
  "data": null,
  "timestamp": "2024-02-27T10:30:45.123456"
}
```

---

### 3. Process Refund
**Endpoint**: `POST /bank/refund`

**Request Headers**:
- `x-api-key`: upi-dispute-resolver-secret-key-2024 (required)
- `Content-Type`: application/json

**Request Body**:
```json
{
  "transaction_id": "TXN20240101001",
  "reason": "Customer requested refund"
}
```

**Business Logic**:
- If transaction status is SUCCESS → Creates refund record and updates transaction to REFUNDED
- If transaction status is FAILED → Returns 400: "Refund not allowed for FAILED transactions"
- If transaction not found → Returns 404

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Refund processed successfully",
  "data": {
    "refund_id": 1,
    "transaction_id": "TXN20240101001",
    "amount": 1000.50,
    "refund_timestamp": "2024-02-27T10:35:20.654321",
    "status": "SUCCESS",
    "reason": "Customer requested refund"
  },
  "timestamp": "2024-02-27T10:35:20.654321"
}
```

**Error Response** (400 Bad Request):
```json
{
  "success": false,
  "message": "Refund not allowed for FAILED transactions",
  "data": null,
  "timestamp": "2024-02-27T10:35:20.654321"
}
```

---

### 4. Fetch All Refunds
**Endpoint**: `GET /bank/refunds`

**Request Headers**:
- `x-api-key`: upi-dispute-resolver-secret-key-2024 (required)

**Response** (200 OK):
```json
{
  "success": true,
  "message": "Refunds retrieved successfully",
  "data": [
    {
      "refund_id": 1,
      "transaction_id": "TXN20240101001",
      "amount": 1000.50,
      "refund_timestamp": "2024-02-27T10:35:20.654321",
      "status": "SUCCESS",
      "reason": "Customer requested refund"
    },
    {
      "refund_id": 2,
      "transaction_id": "TXN20240101002",
      "amount": 500.50,
      "refund_timestamp": "2024-02-27T10:40:50.123456",
      "status": "SUCCESS",
      "reason": "Duplicate transaction"
    }
  ],
  "timestamp": "2024-02-27T10:40:50.123456"
}
```

---

## Database Schema

### transactions table
```sql
CREATE TABLE transactions (
    transaction_id VARCHAR(255) PRIMARY KEY,
    amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    payer_id VARCHAR(255),
    payee_id VARCHAR(255),
    description TEXT
);
```

### refunds table
```sql
CREATE TABLE refunds (
    refund_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    refund_timestamp TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
);
```

## Configuration (application.yml)

```yaml
spring:
  application:
    name: mock-bank-api
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  datasource:
    url: jdbc:h2:mem:mockbankdb
    driverClassName: org.h2.Driver
    username: sa
    password: ""
  h2:
    console:
      enabled: true
      path: /h2-console

server:
  port: 8080

app:
  api-key: "upi-dispute-resolver-secret-key-2024"
```

**Key Properties**:
- `ddl-auto: create-drop` → Recreates schema on startup (suitable for testing)
- H2 in-memory database (data lost on restart)
- H2 Console available at `/h2-console` for debugging

## API Authentication

All endpoints under `/bank/` require API key authentication via the `x-api-key` header.

**Valid API Key**: `upi-dispute-resolver-secret-key-2024`

**Example Request**:
```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240101001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

**Missing API Key Response** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Missing API key in header: x-api-key",
  "data": null,
  "timestamp": "2024-02-27T10:30:45.123456"
}
```

## Exception Handling

The application uses a global exception handler (`GlobalExceptionHandler`) to provide consistent error responses:

| Exception | HTTP Status | Message |
|-----------|------------|---------|
| `TransactionNotFoundException` | 404 | Transaction not found |
| `InvalidRefundException` | 400 | Refund not allowed for FAILED transactions |
| `UnauthorizedException` | 401 | Invalid/Missing API key |
| `RuntimeException` | 500 | An error occurred |
| Generic `Exception` | 500 | An unexpected error occurred |

## Sample Data Initialization

On application startup, the `DataInitializer` bean seeds sample transactions:

1. **TXN20240101001** - SUCCESS ($1000.00)
2. **TXN20240101002** - SUCCESS ($500.50)
3. **TXN20240101003** - FAILED ($250.00)
4. **TXN20240101004** - SUCCESS ($2000.00)
5. **TXN20240101005** - FAILED ($150.00)

## Swagger/OpenAPI Documentation

Once the application is running, API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

The Swagger UI provides:
- Interactive API explorer
- Request/response schemas
- Try-it-out functionality
- Parameter descriptions

## Integration with Python Dispute Resolution Agent

The Mock Bank API integrates with a Python-based Dispute Resolution Agent that:

### 1. Fetches Transaction Details
The Python agent calls:
```
GET /bank/transaction/{transactionId}
```
To retrieve transaction status and details for verification

### 2. Triggers Refunds
The Python agent calls:
```
POST /bank/refund
{
  "transaction_id": "TXN20240101001",
  "reason": "Dispute resolved - auto-refund"
}
```
To initiate refunds for successfully resolved disputes

### Data Flow
```
┌─────────────────────────────────────────┐
│  Python Dispute Resolution Agent        │
│     (Verification Logic)                │
└──────────────┬──────────────────────────┘
               │
               ├─→ GET /bank/transaction/{id}
               │   (Check transaction details)
               │   ↓
               │   Mock Bank API
               │   ↑
               │   (Returns transaction status)
               │
               └─→ POST /bank/refund
                   (If dispute is valid)
                   ↓
                   Mock Bank API
                   ↑
                   (Creates refund record)
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Build
```bash
cd mock-bank-api
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

Or:
```bash
java -jar target/mock-bank-api-1.0.0.jar
```

### Access Points
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

## Testing with cURL

See the `CURL_COMMANDS.md` file for comprehensive cURL commands for testing all endpoints.

## Key Features

✅ **Layered Architecture** - Clean separation of concerns  
✅ **REST API** - Standards-compliant endpoints  
✅ **Global Exception Handling** - Consistent error responses  
✅ **API Key Authentication** - Security via x-api-key header  
✅ **OpenAPI/Swagger** - Interactive API documentation  
✅ **DTOs** - Decoupled request/response models  
✅ **H2 Database** - In-memory for easy testing  
✅ **Sample Data** - Pre-loaded test transactions  
✅ **Production-like** - Ready for deployment

## Notes

- This is a mock bank for demonstration purposes only
- Data is lost on application restart (H2 in-memory database)
- For production, replace H2 with PostgreSQL or MySQL
- API key is stored in configuration (use environment variables in production)
