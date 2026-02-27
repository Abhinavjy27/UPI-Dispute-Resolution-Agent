# API Contracts & Database Schema
## All Endpoints, Enums, Database Tables, and Data Models

---

## Part 1: Naming Conventions (Critical!)

### JSON API (camelCase)
```json
{
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "fraudScoreValue": 0.28,
  "riskLevelName": "LOW",
  "statusCode": 200,
  "errorMessage": null,
  "createdAt": "2026-02-27T14:32:00Z",
  "beneficiaryDetails": {
    "userId": "user-123",
    "accountNumber": "123456789"
  }
}
```

**Rules:**
- All keys: `camelCase`
- Timestamps: ISO 8601 with Z (UTC)
- Money: Include decimals (5000.50)
- Booleans: lowercase (true/false)
- Arrays: plural names (disputes, users)

### Database (snake_case)
```sql
user_id UUID
upi_tx_id VARCHAR(255)
fraud_score_value DECIMAL(3,2)
risk_level_name VARCHAR(20)
created_at TIMESTAMP
beneficiary_details JSONB
```

**Rules:**
- All column names: `snake_case`
- Table names: plural, lowercase
- ID column: always `id` (primary key)
- Foreign keys: `{table}_id` (user_id, dispute_id)
- Timestamps: `created_at`, `updated_at`
- Status columns: `{entity}_status`

### IDs (Always UUID v4)
```
Format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
Example: 550e8400-e29b-41d4-a716-446655440000

In API Response (optional prefix):
- "disputeId": "disp-550e8400-e29b-41d4-a716-446655440000"
- "userId": "user-123456"

In Database (no prefix):
- Store raw UUID in id column
```

### Monetary Amounts
```
Database: DECIMAL(15,2)
Example: 5000.50 (₹5000.50)
Always rupees, never paise

JSON API:
Number: 5000.50
String (if high precision): "5000.50"

Display:
₹5,000.50
```

---

## Part 2: Enums (Constants in Code)

### DisputeStatus (12 States)

```java
public enum DisputeStatus {
    SUBMITTED,          // User just created dispute
    BANK_VERIFYING,     // Checking with bank
    VERIFIED,           // Bank confirmed tx failure
    FRAUD_SCORING,      // Running ML model
    SCORED,             // ML model finished
    APPROVED,           // Decision: REFUND ✅
    REJECTED,           // Decision: NO REFUND ❌
    PENDING_REVIEW,     // Waiting manual review
    REFUND_PROCESSING,  // NEFT initiated
    REFUNDED,           // Money credited ✓
    FAILED,             // Error in process
    CANCELLED           // User cancelled
}
```

**Valid Transitions (Only):**
```
SUBMITTED
  ↓
BANK_VERIFYING
  ↓
VERIFIED
  ↓
FRAUD_SCORING
  ↓
SCORED
  ├─→ APPROVED (if score LOW)
  ├─→ PENDING_REVIEW (if score MEDIUM)
  └─→ REJECTED (if score HIGH)
  ↓
REFUND_PROCESSING (if APPROVED)
  ↓
REFUNDED
```

### RiskLevel (From ML Score)

```java
public enum RiskLevel {
    LOW,     // Score 0.00 - 0.30 → Auto-approve ✅
    MEDIUM,  // Score 0.31 - 0.70 → Manual review ⏳
    HIGH     // Score 0.71 - 1.00 → Auto-reject ❌
}
```

### Other Enums

```java
// TransactionStatus
PENDING, SUCCESS, FAILED, DISPUTED, VERIFIED

// RefundStatus
PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED

// KYCStatus
PENDING, VERIFIED, REJECTED, SUSPENDED

// UserRole
USER, MERCHANT, ADMIN, COMPLIANCE
```

---

## Part 3: Core API Endpoints

### Endpoint 1: Create Dispute
```
POST /api/v1/disputes

Headers:
  Content-Type: application/json
  Authorization: Bearer {JWT_TOKEN}
  Idempotency-Key: {UUID}  ← Same request = same response

Request Body:
{
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "reason": "Transaction failed, money not received",
  "merchantId": "ABC123",
  "deviceId": "phone-uuid"
}

Response 201 (Created):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "reason": "Transaction failed, money not received",
  "status": "SUBMITTED",
  "createdAt": "2026-02-27T14:32:00Z",
  "updatedAt": "2026-02-27T14:32:00Z",
  "estimatedResolutionTime": "2026-02-27T18:32:00Z"
}

Error 400 (Bad Request):
{
  "statusCode": 400,
  "errorMessage": "Invalid UPI format or amount <= 0",
  "timestamp": "2026-02-27T14:32:00Z"
}

Error 409 (Conflict - Duplicate):
{
  "statusCode": 409,
  "errorMessage": "Dispute with same UPI TX ID already exists",
  "existingDisputeId": "550e8400-e29b-41d4-a716-446655440000"
}

Error 401 (Unauthorized):
{
  "statusCode": 401,
  "errorMessage": "Invalid JWT token"
}
```

**Backend Logic:**
1. Validate JWT (Spring Security)
2. Parse & validate request
3. Check Idempotency-Key in Redis
   - If exists → return cached response
   - If not → continue
4. Check duplicate UPI TX ID in database
   - If exists → 409 error
   - If not → continue
5. Create Dispute entity (status = SUBMITTED)
6. Save to PostgreSQL
7. Store Idempotency-Key in Redis (TTL: 24 hours)
8. Publish event: `disputes.created` → RabbitMQ
9. Return 201 response

---

### Endpoint 2: Get Dispute Detail
```
GET /api/v1/disputes/{disputeId}

Headers:
  Authorization: Bearer {JWT_TOKEN}

Path Parameters:
  disputeId: UUID (e.g., 550e8400-e29b-41d4-a716-446655440000)

Response 200 (OK):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-123",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "reason": "Transaction failed",
  "status": "APPROVED",
  "fraudScore": {
    "score": 0.28,
    "riskLevel": "LOW",
    "reasons": [
      "Amount within normal range",
      "Time of day consistent",
      "User trusted (10+ successful txs)"
    ],
    "shapValues": {
      "amount": 0.05,
      "timeOfDay": 0.08,
      "userHistory": 0.15
    }
  },
  "transaction": {
    "bankTxId": "NEFT123456",
    "merchantId": "ABC123",
    "verificationStatus": "VERIFIED",
    "verifiedAt": "2026-02-27T14:33:00Z"
  },
  "refund": {
    "id": "ref-550e8400",
    "status": "COMPLETED",
    "neftReference": "12345NEFT",
    "processedAt": "2026-02-27T14:50:00Z"
  },
  "createdAt": "2026-02-27T14:32:00Z",
  "updatedAt": "2026-02-27T14:50:00Z"
}

Error 404 (Not Found):
{
  "statusCode": 404,
  "errorMessage": "Dispute not found"
}

Error 401 (Unauthorized):
{
  "statusCode": 401,
  "errorMessage": "Invalid JWT token"
}
```

**Backend Logic:**
1. Validate JWT
2. Query PostgreSQL: SELECT * FROM disputes WHERE id = {disputeId}
3. If not found → 404
4. Include relationships (fraudScore, transaction, refund)
5. Return 200 response

---

### Endpoint 3: List Disputes (with Pagination & Filters)
```
GET /api/v1/disputes?page=0&size=20&status=APPROVED&riskLevel=LOW

Query Parameters:
  page: int (default: 0) ← 0-indexed
  size: int (default: 20) ← items per page
  status: string (optional) ← SUBMITTED, APPROVED, REJECTED, etc
  riskLevel: string (optional) ← LOW, MEDIUM, HIGH
  sortBy: string (optional) ← createdAt (default: desc)

Response 200 (OK):
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "upiTxId": "314159265358979@upi",
      "amount": 5000.50,
      "status": "APPROVED",
      "riskLevel": "LOW",
      "createdAt": "2026-02-27T14:32:00Z"
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "upiTxId": "271828182845904@upi",
      "amount": 1200.00,
      "status": "APPROVED",
      "riskLevel": "LOW",
      "createdAt": "2026-02-27T15:10:00Z"
    }
  ],
  "totalElements": 45,
  "totalPages": 3,
  "currentPage": 0,
  "pageSize": 20,
  "hasNext": true
}

Error 400 (Bad Request):
{
  "statusCode": 400,
  "errorMessage": "Invalid status value: INVALID_STATUS"
}
```

**Backend Logic:**
1. Parse query parameters
2. Apply filters (status, riskLevel)
3. Order by (createdAt DESC default)
4. Paginate (offset = page * size, limit = size)
5. Query PostgreSQL with filters & pagination
6. Return Page object with metadata

**SQL Generated:**
```sql
SELECT d.*, f.risk_level
FROM disputes d
LEFT JOIN fraud_scores f ON d.id = f.dispute_id
WHERE d.status = 'APPROVED' AND f.risk_level = 'LOW'
ORDER BY d.created_at DESC
LIMIT 20 OFFSET 0;
```

---

### Endpoint 4: Get Timeline (Status History)
```
GET /api/v1/disputes/{disputeId}/timeline

Response 200 (OK):
[
  {
    "timestamp": "2026-02-27T14:32:00Z",
    "event": "dispute_created",
    "status": "SUBMITTED",
    "message": "Dispute submitted successfully",
    "details": {
      "amount": 5000.50
    }
  },
  {
    "timestamp": "2026-02-27T14:33:00Z",
    "event": "bank_verified",
    "status": "VERIFIED",
    "message": "Bank confirmed transaction failure",
    "details": {
      "bankTxId": "NEFT123456",
      "merchant": "ABC Store"
    }
  },
  {
    "timestamp": "2026-02-27T14:37:00Z",
    "event": "fraud_scored",
    "status": "SCORED",
    "message": "Fraud detection complete",
    "details": {
      "score": 0.28,
      "riskLevel": "LOW"
    }
  },
  {
    "timestamp": "2026-02-27T14:38:00Z",
    "event": "approved",
    "status": "APPROVED",
    "message": "Dispute auto-approved (low fraud risk)",
    "details": {
      "approvalReason": "LOW_RISK_AUTO_APPROVE"
    }
  },
  {
    "timestamp": "2026-02-27T14:39:00Z",
    "event": "refund_initiated",
    "status": "REFUND_PROCESSING",
    "message": "Refund initiated via NEFT",
    "details": {
      "neftReference": "12345NEFT"
    }
  },
  {
    "timestamp": "2026-02-27T14:50:00Z",
    "event": "refunded",
    "status": "REFUNDED",
    "message": "Refund completed, money credited to account",
    "details": {
      "neftReference": "12345NEFT"
    }
  }
]

Error 404 (Not Found):
{
  "statusCode": 404,
  "errorMessage": "Dispute not found"
}
```

**Backend Logic:**
1. Query audit_logs where entity_id = disputeId
2. Order by created_at ASC
3. Map each audit log entry to timeline event
4. Return as JSON array

---

### Endpoint 5: Get Admin Analytics (Dashboard Data)
```
GET /api/v1/admin/analytics?startDate=2026-02-01&endDate=2026-02-28

Query Parameters:
  startDate: ISO 8601 (optional)
  endDate: ISO 8601 (optional)

Response 200 (OK):
{
  "summary": {
    "totalDisputes": 450,
    "approvedCount": 427,
    "rejectedCount": 18,
    "pendingReviewCount": 5,
    "totalRefundedAmount": 2134500.50
  },
  "riskDistribution": {
    "LOW": 427,
    "MEDIUM": 18,
    "HIGH": 5
  },
  "resolutionMetrics": {
    "averageResolutionTimeMinutes": 45,
    "median": 38,
    "p95": 120,
    "p99": 180
  },
  "fraudMetrics": {
    "fraudDetectionRate": 0.96,
    "falsePositiveRate": 0.04,
    "highRiskDisputes": 5
  },
  "refundMetrics": {
    "totalRefundedAmount": 2134500.50,
    "averageRefundAmount": 5000.00,
    "neftSuccessRate": 0.99
  },
  "topReasons": [
    {
      "reason": "Transaction failed",
      "count": 350
    },
    {
      "reason": "Money not received",
      "count": 80
    },
    {
      "reason": "Duplicate charge",
      "count": 20
    }
  ]
}

Error 403 (Forbidden - Admin only):
{
  "statusCode": 403,
  "errorMessage": "Only ADMIN and COMPLIANCE roles allowed"
}
```

**Backend Logic:**
1. Check user role (must be ADMIN or COMPLIANCE)
2. Query disputes table with date filters
3. Aggregate by status, risk_level
4. Calculate metrics (avg, median, percentiles)
5. Query top dispute reasons
6. Return JSON response (can be cached in Redis for 5 minutes)

---

## Part 4: RabbitMQ Events (Async Communication)

### Event 1: disputes.created
Published when user submits dispute

```json
{
  "eventId": "event-uuid",
  "eventType": "dispute_created",
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "userId": "user-123",
  "merchantId": "ABC123",
  "deviceId": "phone-uuid",
  "timestamp": "2026-02-27T14:32:00Z",
  "source": "spring-boot-api"
}

Queue: fraud-scoring-queue
Consumer: FastAPI fraud service + Spring Boot bank verifier
```

### Event 2: disputes.verified
Published after bank confirms transaction

```json
{
  "eventId": "event-uuid",
  "eventType": "dispute_verified",
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "bankTxId": "NEFT123456",
  "merchant": "ABC Store",
  "verifiedAt": "2026-02-27T14:33:00Z",
  "source": "spring-boot-api"
}

Queue: fraud-scoring-queue
Consumer: FastAPI fraud ML service
```

### Event 3: disputes.scored
Published after ML model scores fraud risk

```json
{
  "eventId": "event-uuid",
  "eventType": "dispute_scored",
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "fraudScore": 0.28,
  "riskLevel": "LOW",
  "reasons": ["Amount ok", "User trusted"],
  "shapValues": {"amount": 0.05, "time": 0.08},
  "timestamp": "2026-02-27T14:37:00Z",
  "source": "fastapi-fraud-service"
}

Queue: refund-decision-queue
Consumer: Spring Boot decision engine
```

### Event 4: disputes.approved / disputes.rejected
Published after decision made

```json
{
  "eventId": "event-uuid",
  "eventType": "dispute_approved",
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "decision": "APPROVED",
  "reason": "LOW_RISK_AUTO",
  "timestamp": "2026-02-27T14:38:00Z",
  "source": "spring-boot-decision-engine"
}

Queue: refund-processing-queue
Consumer: Spring Boot refund service
```

### Event 5: refunds.initiated
Published when NEFT refund started

```json
{
  "eventId": "event-uuid",
  "eventType": "refund_initiated",
  "refundId": "ref-550e8400",
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 5000.50,
  "neftReference": "12345NEFT",
  "timestamp": "2026-02-27T14:39:00Z",
  "source": "spring-boot-refund-service"
}

Queue: notification-queue
Consumer: Email/SMS notification service
```

---

## Part 5: Database Schema (SQL)

### Table: users
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  upi_id VARCHAR(255) UNIQUE NOT NULL,
  phone VARCHAR(10) UNIQUE NOT NULL,
  kyc_status VARCHAR(20) DEFAULT 'PENDING',
  device_ids JSONB,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_upi_id ON users(upi_id);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_kyc_status ON users(kyc_status);
```

### Table: disputes
```sql
CREATE TABLE disputes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  upi_tx_id VARCHAR(255) UNIQUE NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  reason VARCHAR(500),
  status VARCHAR(50) DEFAULT 'SUBMITTED',
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_disputes_user_id ON disputes(user_id);
CREATE INDEX idx_disputes_status ON disputes(status);
CREATE INDEX idx_disputes_created_at ON disputes(created_at DESC);
CREATE INDEX idx_disputes_upi_tx_id ON disputes(upi_tx_id);
```

### Table: transactions
```sql
CREATE TABLE transactions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  dispute_id UUID NOT NULL REFERENCES disputes(id),
  bank_tx_id VARCHAR(255),
  merchant_id VARCHAR(255),
  verification_status VARCHAR(20) DEFAULT 'PENDING',
  verified_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_transactions_dispute_id ON transactions(dispute_id);
CREATE INDEX idx_transactions_bank_tx_id ON transactions(bank_tx_id);
```

### Table: fraud_scores
```sql
CREATE TABLE fraud_scores (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  dispute_id UUID NOT NULL REFERENCES disputes(id),
  score DECIMAL(3,2),
  risk_level VARCHAR(20),
  shap_values JSONB,
  explanation VARCHAR(500),
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_fraud_scores_dispute_id ON fraud_scores(dispute_id);
CREATE INDEX idx_fraud_scores_risk_level ON fraud_scores(risk_level);
```

### Table: refunds
```sql
CREATE TABLE refunds (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  dispute_id UUID NOT NULL REFERENCES disputes(id),
  amount DECIMAL(15,2),
  status VARCHAR(20) DEFAULT 'PENDING',
  neft_reference VARCHAR(255),
  processed_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_refunds_dispute_id ON refunds(dispute_id);
CREATE INDEX idx_refunds_status ON refunds(status);
```

### Table: audit_logs
```sql
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  action VARCHAR(255),
  entity_type VARCHAR(50),
  entity_id UUID,
  old_value JSONB,
  new_value JSONB,
  changed_by VARCHAR(255),
  ip_address VARCHAR(45),
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
```

### Table: configs
```sql
CREATE TABLE configs (
  id SERIAL PRIMARY KEY,
  config_key VARCHAR(255) UNIQUE NOT NULL,
  config_value VARCHAR(255),
  description VARCHAR(500),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Insert default configs
INSERT INTO configs (config_key, config_value) VALUES
  ('fraud_threshold_low', '0.30'),
  ('fraud_threshold_medium', '0.70'),
  ('auto_refund_enabled', 'true'),
  ('max_refund_amount', '100000'),
  ('refund_timeout_hours', '24');
```

---

## Part 6: Error Responses (Standard Format)

All errors follow this format (regardless of endpoint):

```json
{
  "statusCode": 400,
  "errorMessage": "Invalid UPI format",
  "errorCode": "INVALID_UPI_FORMAT",
  "timestamp": "2026-02-27T14:32:00Z",
  "path": "/api/v1/disputes",
  "details": [
    {
      "field": "upiTxId",
      "message": "Must be in format XXX@upi"
    }
  ]
}
```

**Common HTTP Status Codes:**
```
200 OK                    ← Success
201 Created              ← Resource created
400 Bad Request          ← Invalid input
401 Unauthorized         ← Invalid JWT
403 Forbidden            ← Insufficient permissions
404 Not Found            ← Resource doesn't exist
409 Conflict             ← Duplicate resource
500 Internal Server Error ← Server error
```

---

## Part 7: Authentication & Authorization

### JWT Token Format
```
Header.Payload.Signature

Payload:
{
  "sub": "user-123",
  "role": "USER",
  "iat": 1677000000,
  "exp": 1677003600,
  "iss": "upi-dispute-agent"
}

Token Lifetime: 1 hour
Refresh Token: 30 days
```

### Roles & Permissions
```
USER:        Create disputes, view own disputes
MERCHANT:    View disputes about own transactions
ADMIN:       View all disputes, analytics
COMPLIANCE:  View sensitive data, audit logs
```

---

## Summary

**4 Core Endpoints:**
1. POST /api/v1/disputes ← Create dispute
2. GET /api/v1/disputes/{id} ← Get detail
3. GET /api/v1/disputes ← List + filter
4. GET /api/v1/disputes/{id}/timeline ← History

**5 RabbitMQ Events:**
1. disputes.created ← Dispute submitted
2. disputes.verified ← Bank verified
3. disputes.scored ← ML fraud scored
4. disputes.approved/rejected ← Decision made
5. refunds.initiated ← Refund started

**6 Database Tables:**
1. users ← Customer data
2. disputes ← Core disputes
3. transactions ← Bank data
4. fraud_scores ← ML results
5. refunds ← Refund tracking
6. audit_logs ← Compliance

**Next: Read TASK_BREAKDOWN.md for hour-by-hour implementation tasks.**
