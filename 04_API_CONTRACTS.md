# 04_API_CONTRACTS

## 🔌 REST API Endpoints

### **Endpoint 1: File Dispute**

**Route:** `POST /api/disputes`

**Request Body:**
```json
{
    "transaction_id": "TXN20260227123456",
    "merchant_upi": "amazon@upi",
    "amount": 5000,
    "customer_phone": "+919876543210"
}
```

**Request Validation:**
```
transaction_id:
  ├─ Type: String
  ├─ Required: Yes
  ├─ Format: TXN followed by 14+ digits
  ├─ Example: TXN20260227123456
  └─ Error if: Not TXN format

merchant_upi:
  ├─ Type: String
  ├─ Required: Yes
  ├─ Format: username@bank
  ├─ Valid: xxx@upi, xxx@bank, xxx@okhdfcbank, etc.
  ├─ Example: amazon@upi or seller@okaxis
  └─ Error if: Invalid UPI format

amount:
  ├─ Type: Integer (in ₹)
  ├─ Required: Yes
  ├─ Range: 1 - 100,000
  ├─ Example: 5000 (not "₹5000")
  └─ Error if: < 1 or > 100,000

customer_phone:
  ├─ Type: String
  ├─ Required: Yes
  ├─ Format: +91 followed by 10 digits
  ├─ Example: +919876543210
  └─ Error if: Invalid Indian phone format
```

**Success Response (200 OK):**
```json
{
    "success": true,
    "dispute_id": "DIS_1709028600",
    "status": "REFUND_INITIATED",
    "amount": 5000,
    "neft_reference": "NEFT20260227123456",
    "expected_settlement": "2026-02-28T09:00:00Z",
    "message": "Refund initiated. Money by tomorrow 9 AM",
    "created_at": "2026-02-27T14:30:00Z",
    "verification_completed_at": "2026-02-27T14:32:00Z"
}
```

**Error Response (400 Bad Request):**
```json
{
    "success": false,
    "error": "INVALID_INPUT",
    "message": "Transaction ID must be in format TXN followed by 14+ digits",
    "field": "transaction_id",
    "details": "Received: 'TXN123'"
}
```

**Error Response (422 Unprocessable Entity) - Rejected Claim:**
```json
{
    "success": false,
    "error": "CLAIM_REJECTED",
    "message": "We verified that money was already received by merchant",
    "status": "REJECTED",
    "details": {
        "customer_bank_debited": true,
        "merchant_bank_credited": true,
        "amount_credited": 5000
    }
}
```

**Error Response (202 Accepted) - Manual Review:**
```json
{
    "success": true,
    "dispute_id": "DIS_1709028601",
    "status": "MANUAL_REVIEW",
    "message": "This case needs manual investigation. Our team will review within 24 hours.",
    "reason": "Conflicting information from banks - cannot auto-approve",
    "reference_number": "MR_1709028601"
}
```

---

### **Endpoint 2: Get Dispute Status**

**Route:** `GET /api/disputes/{dispute_id}`

**Path Parameters:**
```
dispute_id: String (DIS_1709028600)
```

**Success Response (200 OK):**
```json
{
    "dispute_id": "DIS_1709028600",
    "transaction_id": "TXN20260227123456",
    "merchant_upi": "amazon@upi",
    "customer_phone": "+919876543210",
    "amount": 5000,
    "status": "REFUND_INITIATED",
    "status_details": [
        {
            "stage": "VERIFIED_FAILURE",
            "timestamp": "2026-02-27T14:32:00Z",
            "description": "Money left customer account, didn't reach merchant"
        },
        {
            "stage": "AUTO_APPROVED",
            "timestamp": "2026-02-27T14:32:30Z",
            "description": "Auto-approval granted (verified failure)"
        },
        {
            "stage": "REFUND_INITIATED",
            "timestamp": "2026-02-27T14:33:00Z",
            "description": "NEFT initiated to customer account"
        }
    ],
    "verification_results": {
        "customer_bank_debited": true,
        "customer_bank_amount": 5000,
        "merchant_bank_credited": false,
        "merchant_bank_amount": 0,
        "confidence_score": 0.99
    },
    "neft_reference": "NEFT20260227123456",
    "refund_amount": 5000,
    "expected_settlement": "2026-02-28T09:00:00Z",
    "settled_at": null,
    "created_at": "2026-02-27T14:30:00Z",
    "verified_at": "2026-02-27T14:32:00Z",
    "refund_initiated_at": "2026-02-27T14:33:00Z",
    "updated_at": "2026-02-27T14:35:00Z"
}
```

**Error Response (404 Not Found):**
```json
{
    "success": false,
    "error": "NOT_FOUND",
    "message": "Dispute DIS_1709028699 not found"
}
```

---

### **Endpoint 3: Risk Score (Optional)**

**Route:** `POST /api/risk-score`

Only if implementing optional risk feature.

**Request Body:**
```json
{
    "merchant_upi": "amazon@upi",
    "amount": 50000,
    "network_type": "4G",
    "time_of_day": "02:30",
    "device_type": "Android",
    "customer_transaction_count": 45
}
```

**Success Response (200 OK):**
```json
{
    "merchant_upi": "amazon@upi",
    "amount": 50000,
    "risk_score": 72,
    "risk_level": "HIGH",
    "risk_factors": {
        "merchant_failure_rate": 0.052,
        "amount_risk": "HIGH (>₹50K)",
        "time_of_day_risk": "HIGH (2:30 AM)",
        "network_type_risk": "MEDIUM (4G)",
        "device_type_risk": "LOW",
        "customer_history_risk": "LOW (45 txns)"
    },
    "suggestions": [
        {
            "action": "Use WiFi instead of 4G",
            "expected_risk_reduction": 0.35,
            "new_risk_score": 37,
            "impact": "Risk drops from 72% to 37%"
        },
        {
            "action": "Try at 10 AM instead of 2:30 AM",
            "expected_risk_reduction": 0.44,
            "new_risk_score": 28,
            "impact": "Risk drops from 72% to 28%"
        },
        {
            "action": "Split into two ₹25K transactions",
            "expected_risk_reduction": 0.25,
            "new_risk_score": 47,
            "impact": "Risk drops from 72% to 47%"
        },
        {
            "action": "Use NEFT instead of UPI",
            "expected_risk_reduction": 0.67,
            "new_risk_score": 5,
            "impact": "Risk drops from 72% to 5% (slower but safer)"
        }
    ]
}
```

---

## 📊 Database Schema

### **Table: disputes**

```sql
CREATE TABLE disputes (
    id VARCHAR(50) PRIMARY KEY,                          -- DIS_1709028600
    transaction_id VARCHAR(50) NOT NULL UNIQUE,          -- TXN20260227123456
    merchant_upi VARCHAR(255) NOT NULL,                  -- amazon@upi
    customer_phone VARCHAR(20) NOT NULL,                 -- +919876543210
    amount FLOAT NOT NULL,                               -- 5000
    
    status VARCHAR(50) NOT NULL,                         -- PENDING, VERIFIED_FAILURE, etc.
    approval_status VARCHAR(50),                         -- AUTO_APPROVED, MANUAL_REVIEW, REJECTED
    confidence_score FLOAT,                              -- 0.99 (0-1)
    
    customer_bank_debited BOOLEAN,                       -- true
    customer_bank_amount FLOAT,                          -- 5000
    customer_bank_response TEXT,                         -- JSON string
    
    merchant_bank_credited BOOLEAN,                      -- false
    merchant_bank_amount FLOAT,                          -- 0
    merchant_bank_response TEXT,                         -- JSON string
    
    neft_reference VARCHAR(50),                          -- NEFT20260227123456
    refund_amount FLOAT,                                 -- 5000
    
    created_at DATETIME NOT NULL,                        -- 2026-02-27 14:30:00
    verified_at DATETIME,                                -- 2026-02-27 14:32:00
    refund_initiated_at DATETIME,                        -- 2026-02-27 14:33:00
    expected_settlement_at DATETIME,                     -- 2026-02-28 09:00:00
    settled_at DATETIME,                                 -- NULL until actual settlement
    updated_at DATETIME NOT NULL,                        -- auto-updated
    
    INDEX idx_status (status),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_created_at (created_at)
);
```

### **Table: transactions (Optional - for analytics)**

```sql
CREATE TABLE transactions (
    id VARCHAR(50) PRIMARY KEY,
    dispute_id VARCHAR(50) NOT NULL,
    merchant_id VARCHAR(100),
    merchant_upi VARCHAR(255),
    amount FLOAT,
    
    failure_category VARCHAR(100),  -- GATEWAY_TIMEOUT, BANK_DOWN, etc.
    failure_reason TEXT,
    
    created_at DATETIME,
    INDEX idx_dispute_id (dispute_id),
    INDEX idx_merchant_id (merchant_id)
);
```

### **Table: merchants (Optional - for dashboard)**

```sql
CREATE TABLE merchants (
    id VARCHAR(100) PRIMARY KEY,
    merchant_upi VARCHAR(255) UNIQUE NOT NULL,
    merchant_name VARCHAR(255),
    
    total_disputes_filed INT DEFAULT 0,
    disputes_this_month INT DEFAULT 0,
    failure_rate FLOAT DEFAULT 0,  -- 0-1 (e.g., 0.052 = 5.2%)
    
    health_status VARCHAR(50),  -- HEALTHY, WARNING, CRITICAL
    last_failure_at DATETIME,
    
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_merchant_upi (merchant_upi),
    INDEX idx_failure_rate (failure_rate)
);
```

---

## 🧪 Testing with cURL

### **Test 1: File a Dispute (Happy Path)**

```bash
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "TXN20260227123456",
    "merchant_upi": "amazon@upi",
    "amount": 5000,
    "customer_phone": "+919876543210"
  }'

# Expected response:
{
    "success": true,
    "dispute_id": "DIS_1709028600",
    "status": "REFUND_INITIATED",
    "neft_reference": "NEFT20260227123456",
    ...
}
```

### **Test 2: Get Dispute Status**

```bash
curl -X GET http://localhost:8000/api/disputes/DIS_1709028600

# Expected response:
{
    "dispute_id": "DIS_1709028600",
    "status": "REFUND_INITIATED",
    ...
}
```

### **Test 3: Invalid Input**

```bash
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "INVALID",
    "merchant_upi": "notaupi",
    "amount": -5000,
    "customer_phone": "1234"
  }'

# Expected response:
{
    "success": false,
    "error": "INVALID_INPUT",
    "message": "Transaction ID must be in format TXN followed by 14+ digits"
}
```

### **Test 4: Risk Score (if implemented)**

```bash
curl -X POST http://localhost:8000/api/risk-score \
  -H "Content-Type: application/json" \
  -d '{
    "merchant_upi": "amazon@upi",
    "amount": 50000,
    "network_type": "4G",
    "time_of_day": "02:30",
    "device_type": "Android",
    "customer_transaction_count": 45
  }'

# Expected response:
{
    "risk_score": 72,
    "risk_level": "HIGH",
    "suggestions": [...]
}
```

---

## 📝 Postman Collection

If you want to test in Postman, use these URLs:

```
Method: POST
URL: http://localhost:8000/api/disputes
Body (raw JSON):
{
    "transaction_id": "TXN20260227123456",
    "merchant_upi": "amazon@upi",
    "amount": 5000,
    "customer_phone": "+919876543210"
}
```

---

## 🔐 HTTP Status Codes

```
200 OK
├─ Success: Dispute filed and refund initiated
└─ Example: POST /api/disputes returns 200 with success=true

202 ACCEPTED
├─ Partial success: Case escalated for manual review
└─ Example: Conflicting bank responses → Manual review

400 BAD REQUEST
├─ Client error: Invalid input format
└─ Example: amount = -5000, phone = "invalid"

404 NOT FOUND
├─ Resource not found
└─ Example: GET /api/disputes/INVALID_ID

422 UNPROCESSABLE ENTITY
├─ Business logic error: Claim rejected
└─ Example: Money already reached merchant

500 INTERNAL SERVER ERROR
├─ Server error: Database crash, etc.
└─ Should not happen in normal operation

503 SERVICE UNAVAILABLE
├─ Bank API is down
└─ Graceful degradation: Queued for retry
```

---

## 🔄 Data Flow Diagram

```
Frontend                Backend                Databases       External APIs
  │                       │                        │                │
  │──POST /disputes────→  │                        │                │
  │    (4 fields)         │                        │                │
  │                       ├─→ Validate input      │                │
  │                       │    if invalid         │                │
  │                       ├─→ Create record   ────→│ disputes.PENDING
  │                       │    in DB              │                │
  │                       │                       │                │
  │                       ├──────────────────────────→ Bank API 1 (Customer)
  │                       │   verify debit         │     "Was ₹ left?"
  │                       │   (parallel)           │                │
  │                       │                       │                │
  │                       ├──────────────────────────→ Bank API 2 (Merchant)
  │                       │   verify credit        │     "Was ₹ received?"
  │                       │   (parallel)           │                │
  │                       │                       │                │
  │                       ←──────────────────────────→              │
  │                       │   Bank responses       │                │
  │                       │                       │                │
  │                       ├─→ Verify logic:       │                │
  │                       │   ├─ Debit ✓          │                │
  │                       │   └─ Not Credited ✗   │                │
  │                       │   = VERIFIED_FAILURE  │                │
  │                       │                       │                │
  │                       ├─→ Auto-approve        │                │
  │                       │   if clear case       │                │
  │                       │                       │                │
  │                       ├─→ Initiate NEFT   ────→│ Update REFUND_INITIATED
  │                       │   refund              │                │
  │                       │                       │                │
  │                       ├──→ SMS notification    │                │
  │                       │                       │                │
  │←──200 OK───────────────│                       │                │
  │  {dispute_id}          │                       │                │
  │  {neft_ref}            │                       │                │
  │                        │                       │                │
  │──GET /disputes/{id}──→ │                       │                │
  │                        ├─→ Query DB      ─────→│ SELECT * WHERE │
  │                        │                       │   id=...       │
  │←──200 OK───────────────│                       │                │
  │  {all details}         │                       │                │
```

---

## 🚀 Next Step

👉 Read **[05_TECH_STACK_AND_SETUP.md](05_TECH_STACK_AND_SETUP.md)** to set up locally
