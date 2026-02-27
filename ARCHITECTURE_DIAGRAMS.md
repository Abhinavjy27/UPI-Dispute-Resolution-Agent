# System Architecture & Workflow Diagrams

---

## 1. System Architecture Overview

This diagram shows how all components of the UPI Dispute Resolution system interact with each other.

### Architecture Diagram

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                              🖥️  FRONTEND LAYER                              │
│                         React 18 + Vite + Tailwind CSS                        │
│                                                                               │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────────┐       │
│  │                  │  │                  │  │                      │       │
│  │  📝 Dispute      │  │  📊 Tracking     │  │  📈 Admin            │       │
│  │  Submission      │  │  Dashboard       │  │  Analytics           │       │
│  │  Form            │  │                  │  │  Dashboard           │       │
│  │                  │  │  - View status   │  │                      │       │
│  │  - UPI ID        │  │  - Timeline      │  │  - Total disputes    │       │
│  │  - Amount        │  │  - Score details │  │  - Approved count    │       │
│  │  - Reason        │  │  - Bank details  │  │  - High-risk count   │       │
│  │                  │  │                  │  │  - Charts & metrics  │       │
│  └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────────┘       │
│           │                     │                     │                      │
│           │        Redux State Management             │                      │
│           └──────────────────┬──────────────────────┘                        │
│                              │                                               │
│                        [Redux Store]                                         │
└──────────────────────────────┬──────────────────────────────────────────────┘
                               │
                               │ HTTP/REST (JWT Authentication)
                               │ Axios HTTP Client
                               ▼
┌───────────────────────────────────────────────────────────────────────────────┐
│                    🌐 NETWORK LAYER (Authentication)                         │
│                                                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  JWT Token Validation | Axios Interceptors | CORS Headers           │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────┬──────────────────────────────────────────┘
                                   │
                                   ▼
┌───────────────────────────────────────────────────────────────────────────────┐
│                       ⚙️  BACKEND API LAYER                                  │
│                    Spring Boot 3 + Java 17 (Port 8080)                       │
│                                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ REST Controllers (DisputeController, AdminController)               │   │
│  │  POST   /api/v1/disputes           (Create dispute)                │   │
│  │  GET    /api/v1/disputes/{id}      (Get details)                  │   │
│  │  GET    /api/v1/disputes?...       (List with filtering)          │   │
│  │  GET    /api/v1/admin/analytics    (Admin dashboard)              │   │
│  └────────────────────────────┬───────────────────────────────────────┘   │
│                               │                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Services Layer                                                       │   │
│  │  ├─ DisputeService      (CRUD, status management)                  │   │
│  │  ├─ BankVerificationService  (Call bank API)                       │   │
│  │  ├─ DecisionEngine      (Auto-approve/reject logic)                │   │
│  │  ├─ RefundService       (Process refunds)                          │   │
│  │  └─ AuditService        (Log all actions)                          │   │
│  └────────────────────────────┬───────────────────────────────────────┘   │
│                               │                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │ Repositories (Spring Data JPA)                                       │   │
│  │  ├─ DisputeRepository    (Query disputes table)                     │   │
│  │  ├─ UserRepository       (Query users table)                        │   │
│  │  ├─ RefundRepository     (Query refunds table)                      │   │
│  │  ├─ FraudScoreRepository (Query fraud scores)                       │   │
│  │  └─ AuditLogRepository   (Query audit logs)                         │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                               │
└──────────────┬────────────────────────────────────────┬──────────────────────┘
               │                                        │
               │ Queries/Inserts                        │ Publish Events
               │ (JPA)                                  │ (RabbitMQ)
               ▼                                        ▼
    ┌────────────────────┐        ┌────────────────────────────┐
    │  PostgreSQL 🐘     │        │  RabbitMQ 📬 Event Bus    │
    │  Database Port 5432│        │  (AMQP) Port 5672         │
    │                    │        │                            │
    │ ┌────────────────┐ │        │ Exchanges:                 │
    │ │ users          │ │        │  • disputes (topic)        │
    │ │ disputes       │ │        │  • fraud (topic)           │
    │ │ transactions   │ │        │  • refunds (topic)         │
    │ │ fraud_scores   │ │        │                            │
    │ │ refunds        │ │        │ Queues:                    │
    │ │ audit_logs     │ │        │  • fraud-queue             │
    │ │                │ │        │  • refund-queue            │
    │ │ (ACID)         │ │        │  • notification-queue      │
    │ │ @index user_id │ │        │  • dlq (dead-letter)       │
    │ │ @index status  │ │        │                            │
    │ │ @index created │ │        │ Routing:                   │
    │ └────────────────┘ │        │  disputes.* → fraud-queue  │
    │                    │        │  fraud.* → refund-queue    │
    │                    │        │  refund.* → notify-queue   │
    └────────────────────┘        └──────────────┬─────────────┘
               │                                  │
               │                                  ▼
               │                      ┌─────────────────────────┐
               │                      │ 🤖 ML Service (FastAPI) │
               │                      │ Python 3.11             │
               │ Live Cache Read      │ Port 8000               │
               │ (polling)            │                         │
               │                      │ ┌────────────────────┐ │
               ▼                      │ │ /api/v1/fraud/     │ │
    ┌────────────────────┐           │ │ score              │ │
    │  Redis 🔴 Cache   │           │ │                    │ │
    │  Port 6379        │           │ │ Input:             │ │
    │                    │           │ │ {amount, merchant} │ │
    │ • Session tokens   │           │ │                    │ │
    │ • Idempotency keys │           │ │ Output:            │ │
    │ • Rate limits      │           │ │ {score: 0.0-1.0,  │ │
    │ • Cached queries   │           │ │  riskLevel,        │ │
    │ • Dispute cache    │           │ │  reasons}          │ │
    │                    │           │                      │ │
    │ (Expires: TTL)     │           │ ┌────────────────────┐ │
    └────────────────────┘           │ │ Isolation Forest   │ │
                                     │ │ Anomaly Detector   │ │
                                     │                      │ │
                                     │ ┌────────────────────┐ │
                                     │ │ SHAP Explainer     │ │
                                     │ │ Feature importance │ │
                                     │ └────────────────────┘ │
                                     │                        │
                                     │ Publish fraud.scored   │
                                     │ back to RabbitMQ       │
                                     └────────────┬───────────┘
                                                  │
                                                  │
                                     ┌────────────▼───────────┐
                                     │ 📧 Notification Service│
                                     │ (Email/SMS)            │
                                     │ Consumes:              │
                                     │ • refund.completed     │
                                     │ • dispute.rejected     │
                                     │ Sends user updates     │
                                     └────────────────────────┘

```

---

## 2. System Component Details

### Frontend Components
- **Dispute Submission Form**: React form with input validation
- **Tracking Dashboard**: Real-time status updates with polling (30-second intervals)
- **Admin Analytics**: Charts showing metrics (total disputes, approval rate, avg resolution time)
- **Redux Store**: Centralized state management for disputes, user data, UI state

### Backend Services
- **DisputeController**: Handles HTTP requests for disputes
- **DisputeService**: Business logic for dispute creation and status updates
- **BankVerificationService**: Calls mock bank API to verify transactions
- **DecisionEngine**: Auto-approval/rejection logic based on fraud score
- **RefundService**: NEFT payment processing
- **AuditService**: Logs all actions for compliance

### Database (PostgreSQL)
Located at: `postgres:5432` (Docker service)

**Tables:**
- `users` - Customer KYC information
- `disputes` - Main dispute records
- `transactions` - Bank transaction details
- `fraud_scores` - ML model scores
- `refunds` - Refund processing records
- `audit_logs` - Action audit trail

### Message Queue (RabbitMQ)
Located at: `rabbitmq:5672` (Docker service, UI at port 15672)

**Event Flow:**
```
disputes.created → fraud-queue → ML Service → disputes.scored
     ↓
Back to Backend → decision engine
     ↓
     ├─ AUTO-APPROVE → refund.initiated → refund-queue
     ├─ PENDING_REVIEW → Manual flag
     └─ AUTO-REJECT → fraud detected notification
```

### ML Service (FastAPI)
Located at: `fastapi:8000`

**Endpoint:** `POST /api/v1/fraud/score`

**Input:**
```json
{
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "merchant": "Amazon",
  "transactionTime": "2026-02-27T10:35:00Z"
}
```

**Output:**
```json
{
  "fraudScore": 0.25,
  "riskLevel": "LOW",
  "reasons": ["amount_normal", "time_normal", "merchant_trusted"],
  "confidence": 0.95,
  "modelVersion": "1.0"
}
```

---

## 3. Data Flow Sequence

### 🔴 Step 1: User Submits Dispute

```
┌─────────────────────────────────────────────────────────────────┐
│ User fills form on React Frontend:                              │
│  • UPI TX ID: 314159265358979@upi                              │
│  • Amount: ₹5000.50                                            │
│  • Reason: "Transaction failed but amount debited"             │
│  • Clicks: "Submit Dispute"                                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
        ┌─────────────────────────────────────┐
        │ React Validation (Client-side)      │
        │ ✓ UPI format valid?                 │
        │ ✓ Amount > 0?                       │
        │ ✓ Reason not empty?                 │
        │ ✓ User authenticated?               │
        └──────────┬──────────────────────────┘
                   │ If valid
                   ▼
     ┌─────────────────────────────────────┐
     │ Generate Idempotency Key (UUID)     │
     │ Check Redis for duplicate           │
     │ Cache: idempotency:{uuid} = reply   │
     └──────────────────────────────────────┘
```

### 🟠 Step 2: API Request

```
POST /api/v1/disputes
Headers:
  Authorization: Bearer {JWT_TOKEN}
  Idempotency-Key: {UUID}
  Content-Type: application/json

Body:
{
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "disputeReason": "Transaction failed"
}

Response (HTTP 201):
{
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "SUBMITTED",
  "createdAt": "2026-02-27T10:35:00Z"
}
```

### 🟡 Step 3: Backend Processing

```
Spring Boot Controller receives request
          │
          ├─ Validate JWT Token (Spring Security)
          │  └─ Extract userId from claims
          │
          ├─ Check Idempotency in Redis
          │  └─ If exists → Return cached response (STOP)
          │
          ├─ Validate Request Body
          │  └─ UPI format, amount, reason
          │
          └─ Call DisputeService.createDispute()
               │
               ├─ Create Dispute entity
               │  ├─ Status: SUBMITTED
               │  ├─ fraudScore: NULL
               │  ├─ refundStatus: NULL
               │  └─ createdAt: NOW()
               │
               ├─ Save to PostgreSQL
               │  └─ GET new disputeId (UUID)
               │
               └─ Store in Redis cache
                  └─ idempotency:{uuid} = response (TTL: 24h)
```

### 🟢 Step 4: Event Publishing

```
DisputeService publishes to RabbitMQ:

Exchange: disputes (topic)
Routing Key: disputes.created
Message:
{
  "eventId": "uuid",
  "eventType": "disputes.created",
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "userId": "user-uuid",
  "timestamp": "2026-02-27T10:35:00Z"
}
          │
          └─ RabbitMQ Routes to: fraud-queue
               │
               ├─ Backend Consumer (Bank Verification)
               └─ ML Service Consumer (Feature extraction)
```

### 🔵 Step 5: Bank Verification (Async)

```
RabbitMQ disputes.created event
          │
          ▼
Backend BankVerificationListener receives event
          │
          ├─ Extract disputeId, upiTxId
          │
          ├─ Call Mock Bank API
          │  └─ GET /api/bank/verify-tx?txId=314159265358979@upi
          │
          └─ Bank responds:
               {
                 "verified": true,
                 "merchant": "Amazon",
                 "amount": 5000.50,
                 "timestamp": "2026-02-27T10:00:00Z"
               }
                  │
                  ├─ If verified=true:
                  │  ├─ Update Dispute status: VERIFIED
                  │  ├─ Save to PostgreSQL
                  │  └─ Publish: disputes.verified
                  │
                  └─ If verified=false:
                     ├─ Update status: VERIFICATION_FAILED
                     ├─ Save error reason
                     └─ Send notification to user
```

### 🟣 Step 6: ML Fraud Detection

```
disputes.verified event from RabbitMQ
          │
          ▼
FastAPI FraudScoringListener receives
          │
          ├─ Extract dispute details
          │  ├─ amount: 5000.50
          │  ├─ merchant: "Amazon"
          │  ├─ time: 2026-02-27T10:00:00Z
          │  └─ userId: user-uuid
          │
          ├─ Feature Engineering
          │  ├─ amount_log_scaled: log(5000.50 + 1)
          │  ├─ hour_of_day: 10
          │  ├─ is_high_value: false (< ₹50k)
          │  ├─ merchant_category: "Retail"
          │  └─ days_since_signup: 365
          │
          ├─ Run Isolation Forest Model
          │  └─ Outputs: Anomaly Score (0.0 = normal, 1.0 = anomaly)
          │
          ├─ Convert to Risk Level
          │  ├─ Score 0.0-0.3  → LOW RISK (99% legitimate)
          │  ├─ Score 0.31-0.7 → MEDIUM RISK (60% legitimate)
          │  └─ Score 0.71-1.0 → HIGH RISK (20% legitimate)
          │
          ├─ SHAP Explanation
          │  └─ Why is score 0.25?
          │     ├─ Normal amount ↓
          │     ├─ Trusted merchant ↓
          │     └─ Normal time ↓
          │
          └─ Publish: fraud.scored
               {
                 "disputeId": "550e8400-...",
                 "fraudScore": 0.25,
                 "riskLevel": "LOW",
                 "reasons": ["amount_normal", "merchant_trusted"],
                 "timestamp": "2026-02-27T10:35:30Z"
               }
                  │
                  └─ RabbitMQ routes to: refund-queue
```

### 🟤 Step 7: Auto-Decision Engine

```
fraud.scored event received by Backend
          │
          ▼
DecisionEngine.makeDecision(fraudScore)
          │
          ├─ IF fraudScore < 0.30 (LOW)
          │  │
          │  ├─ Decision: AUTO_APPROVED
          │  ├─ Reason: "Low fraud risk"
          │  │
          │  ├─ Update Dispute:
          │  │  ├─ status: APPROVED
          │  │  ├─ fraudScore: 0.25
          │  │  ├─ decisionAt: NOW()
          │  │  └─ decidedBy: "AUTO_ENGINE"
          │  │
          │  └─ Publish: refund.initiated
          │
          ├─ ELSE IF 0.30 ≤ fraudScore ≤ 0.70 (MEDIUM)
          │  │
          │  ├─ Decision: PENDING_REVIEW
          │  ├─ Reason: "Moderate risk - needs manual review"
          │  │
          │  ├─ Update Dispute:
          │  │  ├─ status: PENDING_REVIEW
          │  │  ├─ fraudScore: 0.50
          │  │  └─ flaggedForReview: true
          │  │
          │  └─ Alert Support Team:
          │     └─ "Dispute #123 flagged for review"
          │
          └─ ELSE IF fraudScore > 0.70 (HIGH)
             │
             ├─ Decision: AUTO_REJECTED
             ├─ Reason: "High fraud probability"
             │
             ├─ Update Dispute:
             │  ├─ status: REJECTED
             │  ├─ fraudScore: 0.85
             │  └─ rejectionReason: "Suspected fraud"
             │
             └─ Notify User:
                └─ "Your dispute was rejected due to fraud indicators"
```

### 🟥 Step 8: Refund Processing

```
refund.initiated event received
          │
          ▼
RefundService.processRefund(disputeId)
          │
          ├─ Validate Dispute status = APPROVED
          │
          ├─ Extract amount from Dispute
          │  └─ Amount: ₹5000.50 (BigDecimal precision)
          │
          ├─ Generate NEFT Reference
          │  └─ NEFT_REF: SPRINGAI000550001 (unique, sequential)
          │
          ├─ Create Refund record
          │  ├─ refundId: UUID
          │  ├─ amount: 5000.50
          │  ├─ status: PROCESSING
          │  ├─ neftRef: SPRINGAI000550001
          │  └─ createdAt: NOW()
          │
          ├─ Call Bank API (Mock)
          │  └─ POST /api/bank/neft-transfer
          │     Request: {amount, neftRef, upiId}
          │
          ├─ If success:
          │  ├─ Update Refund status: COMPLETED
          │  ├─ Update Dispute status: REFUNDED
          │  └─ Publish: refund.completed
          │
          └─ If failure:
             ├─ Update Refund status: FAILED
             ├─ Add to dead-letter queue (retry later)
             └─ Alert support team
```

### 🎯 Step 9: User Notification

```
refund.completed event
          │
          ▼
NotificationService receives
          │
          ├─ Extract user contact info
          │  ├─ userId: user-uuid
          │  ├─ email: user@example.com
          │  └─ phone: +91-9876543210
          │
          ├─ Compose message
          │  └─ "Dispute #ABC resolved!
          │     Refund ₹5000.50 initiated.
          │     Arrives in 2-4 hours via NEFT"
          │
          ├─ Send SMS (optional)
          ├─ Send Email
          └─ Update Dispute notification_sent: true
```

### 🏁 Step 10: Real-Time Status Tracking

```
User clicks "Check Status" or page auto-polls every 30 seconds
          │
          ▼
GET /api/v1/disputes/{disputeId}
Headers: Authorization: Bearer {JWT_TOKEN}
          │
          ▼
Backend DisputeController.getDispute(disputeId)
          │
          ├─ Check Redis cache first
          │  └─ Key: dispute:{disputeId}, TTL: 5 min
          │
          ├─ If cache miss → Query PostgreSQL
          │  ├─ SELECT * FROM disputes WHERE id = disputeId
          │  ├─ JOIN fraud_scores ON disputes.id = fraud_scores.dispute_id
          │  └─ JOIN refunds ON disputes.id = refunds.dispute_id
          │
          ├─ Build response
          │  {
          │    "disputeId": "550e8400-...",
          │    "status": "REFUNDED",
          │    "upiTxId": "314159265358979@upi",
          │    "amount": 5000.50,
          │    "fraudScore": 0.25,
          │    "riskLevel": "LOW",
          │    "refundStatus": "COMPLETED",
          │    "neftRef": "SPRINGAI000550001",
          │    "timeline": [
          │      {time: "10:35:00", status: "SUBMITTED"},
          │      {time: "10:35:15", status: "VERIFIED"},
          │      {time: "10:35:30", status: "SCORED"},
          │      {time: "10:35:35", status: "APPROVED"},
          │      {time: "10:35:45", status: "REFUND_PROCESSING"},
          │      {time: "10:36:00", status: "REFUNDED"}
          │    ]
          │  }
          │
          └─ Cache response for 5 minutes
             │
             ▼
          React receives response
             │
             ├─ Update Redux state
             ├─ Render updated UI
             ├─ Show status: "✅ REFUNDED"
             ├─ Display timeline
             └─ Show bank details & NEFT reference
```

---

## 4. API Endpoints Reference

### Create Dispute
```
POST /api/v1/disputes
Authorization: Bearer {JWT}
Idempotency-Key: {UUID}

Request:
{
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "disputeReason": "Transaction failed"
}

Response (201 Created):
{
  "disputeId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "SUBMITTED",
  "createdAt": "2026-02-27T10:35:00Z"
}

Error (400):
{
  "error": "INVALID_UPI_FORMAT",
  "message": "UPI ID must match pattern: ..."
}
```

### Get Dispute Details
```
GET /api/v1/disputes/{disputeId}
Authorization: Bearer {JWT}

Response (200):
{
  "disputeId": "550e8400-...",
  "userId": "user-123",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "status": "REFUNDED",
  "fraudScore": 0.25,
  "riskLevel": "LOW",
  "refundStatus": "COMPLETED",
  "neftRef": "SPRINGAI000550001",
  "timeline": [{...}],
  "createdAt": "2026-02-27T10:35:00Z",
  "updatedAt": "2026-02-27T10:36:00Z"
}

Error (404):
{
  "error": "DISPUTE_NOT_FOUND",
  "message": "Dispute with ID ... not found"
}
```

### List Disputes with Filtering
```
GET /api/v1/disputes?page=0&size=20&status=APPROVED&riskLevel=LOW
Authorization: Bearer {JWT}

Response (200):
{
  "content": [
    {
      "disputeId": "550e8400-...",
      "amount": 5000.50,
      "status": "APPROVED",
      "fraudScore": 0.25,
      "createdAt": "2026-02-27T10:35:00Z"
    },
    {...}
  ],
  "totalElements": 150,
  "totalPages": 8,
  "currentPage": 0
}
```

### Admin Analytics
```
GET /api/v1/admin/analytics
Authorization: Bearer {JWT}

Response (200):
{
  "totalDisputes": 1250,
  "approvedCount": 950,
  "rejectedCount": 200,
  "pendingReviewCount": 100,
  "avgResolutionHours": 2.3,
  "avgFraudScore": 0.32,
  "highRiskCount": 120,
  "approvalRate": 0.76,
  "topMerchants": ["Amazon", "Flipkart", "Swiggy"],
  "topReasons": ["failed_txn", "duplicate_charge", "unauthorized"]
}
```

---

## 5. Event Flow Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                    UPI DISPUTE RESOLUTION                        │
│                    EVENT-DRIVEN ARCHITECTURE                     │
└──────────────────────────────────────────────────────────────────┘

┌─────────────────────────┐
│ User Submits Dispute    │
│ POST /api/v1/disputes   │
└────────────┬────────────┘
             │
             ▼
    ┌─────────────────┐
    │ CREATE DISPUTE  │
    │ Status: SUBMITTED
    └────────┬────────┘
             │
             │ Publish
             ▼
┌───────────────────────────────────────────┐
│ RabbitMQ Exchange: disputes (topic)       │
│ Routing Key: disputes.created             │
└────────────────┬────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
   ┌─────────────┐  ┌───────────────────┐
   │ Backend     │  │ FastAPI ML        │
   │ Consumer    │  │ Listener          │
   └──────┬──────┘  └────────┬──────────┘
          │                  │
          │ Call Bank API    │ Extract Features
          │                  │
          ▼                  ▼
    ┌─────────────┐  ┌──────────────┐
    │ Bank API    │  │ Isolation    │
    │ Verify TX   │  │ Forest Model │
    └──────┬──────┘  └────────┬─────┘
           │                  │
           │ Update status    │ Score
           │ to: VERIFIED     │
           │                  │
           └────────┬─────────┘
                    │
                    │ Publish
                    ▼
    ┌──────────────────────────────┐
    │ RabbitMQ: disputes.verified  │
    │ Status: VERIFIED             │
    │ With fraud score: 0.25       │
    └────────────┬─────────────────┘
                 │
                 │ Publish
                 ▼
    ┌──────────────────────────────┐
    │ RabbitMQ: fraud.scored       │
    │ Status: SCORED               │
    │ Risk Level: LOW              │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────┐
    │ AUTO-DECISION ENGINE │
    │ Score < 0.3 → APPROVE│
    │ 0.3-0.7 → REVIEW     │
    │ > 0.7 → REJECT       │
    └────────┬─────────────┘
             │
             ├─ Decision: APPROVED
             │
             │ Publish
             ▼
    ┌──────────────────────────────┐
    │ RabbitMQ: refund.initiated   │
    │ Status: APPROVED             │
    │ Initiate refund processing   │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────┐
    │ REFUND PROCESSING    │
    │ Generate NEFT Ref    │
    │ Call Bank NEFT API   │
    └────────┬─────────────┘
             │
             │ Publish
             ▼
    ┌──────────────────────────────┐
    │ RabbitMQ: refund.completed   │
    │ Status: REFUNDED             │
    │ NEFT Transfer Complete       │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────┐
    │ NOTIFY USER          │
    │ SMS/Email            │
    │ Refund successful    │
    └──────────────────────┘
```

---

## 6. Database Schema Diagram

```
┌────────────────────────┐
│      users             │
├────────────────────────┤
│ id (UUID) [PK]         │
│ upi_address (String)   │
│ kyc_status (Enum)      │
│ created_at (Timestamp) │
└────────────┬───────────┘
             │ 1:N
             │
             ▼
┌────────────────────────────┐
│       disputes             │
├────────────────────────────┤
│ id (UUID) [PK]             │
│ user_id (UUID) [FK]        │
│ upi_tx_id (String)         │
│ amount (DECIMAL 15,2)      │
│ status (Enum) [Index]      │
│ fraud_score (Decimal)      │
│ created_at (Timestamp)     │
│ updated_at (Timestamp)     │
└────────┬───────────┬───────┘
         │           │ 1:N
         │           │
         │           ▼
         │  ┌──────────────────────┐
         │  │   fraud_scores       │
         │  ├──────────────────────┤
         │  │ id (UUID) [PK]       │
         │  │ dispute_id (UUID)[FK]│
         │  │ score_value (Float)  │
         │  │ risk_level (Enum)    │
         │  │ reasons_json (Text)  │
         │  │ created_at (TS)      │
         │  └──────────────────────┘
         │
         └─ 1:N
            │
            ▼
   ┌────────────────────────────┐
   │     transactions           │
   ├────────────────────────────┤
   │ id (UUID) [PK]             │
   │ dispute_id (UUID) [FK]     │
   │ bank_tx_id (String)        │
   │ status (Enum)              │
   │ created_at (Timestamp)     │
   └────────────────────────────┘


       ┌─────────────────────┐
       │      refunds        │
       ├─────────────────────┤
       │ id (UUID) [PK]      │
       │ dispute_id (FK)     │
       │ amount (DECIMAL)    │
       │ status (Enum)       │
       │ neft_ref_no (Str)   │
       │ created_at (TS)     │
       └─────────────────────┘


   ┌──────────────────────────┐
   │    audit_logs            │
   ├──────────────────────────┤
   │ id (UUID) [PK]           │
   │ entity_type (String)     │
   │ entity_id (UUID)         │
   │ action (String)          │
   │ user_id (UUID) [FK]      │
   │ old_value (JSON)         │
   │ new_value (JSON)         │
   │ created_at (Timestamp)   │
   └──────────────────────────┘
```

---

## 7. Deployment Architecture

```
┌────────────────────────────────────────────────────┐
│          RAILWAY.APP (Cloud Platform)             │
│                                                    │
│  ┌──────────────────────────────────────────────┐ │
│  │     Container Orchestration (Docker)         │ │
│  │                                              │ │
│  │  ┌────────────┐  ┌────────────── ┌─────────┐ │
│  │  │  Spring    │  │  FastAPI  │   │Postgres │ │
│  │  │  Boot App  │  │  Service  │   │ Database│ │
│  │  │ Port 8080  │  │Port 8000  │   │Port 5432│ │
│  │  └────────────┘  └────────────┘  └─────────┘ │
│  │                                              │
│  │  ┌────────────┐  ┌────────────────────────┐ │
│  │  │  RabbitMQ  │  │   Redis Cache          │ │
│  │  │ Port 5672  │  │   Port 6379            │ │
│  │  └────────────┘  └────────────────────────┘ │
│  │                                              │
│  └──────────────────────────────────────────────┘
│
└────────────────────────────────────────────────────┘
         ▲
         │ Domain: https://yourname-backend.railway.app
         │
    ┌────┴──────────┐
    │               │
    ▼               ▼
┌─────────┐    ┌─────────────┐
│Client   │    │Admin Panel  │
│(React)  │    │(React)      │
└─────────┘    └─────────────┘
```

---

## 8. Development Environment (Docker Compose)

```yaml
version: '3.8'

services:
  ###############
  # PostgreSQL
  ###############
  postgres:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: upi_disputes
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    volumes:
      - postgres_data:/var/lib/postgresql/data

  ###############
  # Spring Boot
  ###############
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq
      - redis
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/upi_disputes
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_REDIS_HOST: redis

  ###############
  # FastAPI
  ###############
  fastapi:
    build: ./ml_service
    ports:
      - "8000:8000"
    depends_on:
      - rabbitmq

  ###############
  # RabbitMQ
  ###############
  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    ports:
      - "5672:5672"    # AMQP
      - "15672:15672"  # Management UI
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

  ###############
  # Redis
  ###############
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  postgres_data:
  rabbitmq_data:
  redis_data:
```

---

## 9. Quick Reference: Component Communication

### Spring Boot → FastAPI
```
POST http://fastapi:8000/api/v1/fraud/score
Request:
{
  "disputeId": "...",
  "amount": 5000.50,
  "merchant": "Amazon",
  "transactionTime": "2026-02-27T10:35:00Z"
}

Response:
{
  "fraudScore": 0.25,
  "riskLevel": "LOW"
}
```

### Spring Boot → RabbitMQ
```
amqp://guest:guest@rabbitmq:5672/
Exchange: disputes (type: topic)
Queue: fraud-queue (binding: disputes.* → fraud-queue)
```

### Spring Boot → PostgreSQL
```
jdbc:postgresql://postgres:5432/upi_disputes
Default JPA: Hibernate
Connection pool: HikariCP (10 connections)
```

### Spring Boot → Redis
```
redis://redis:6379/ (no password)
Cache TTL: 5 minutes
Session: 24 hours
Idempotency keys: 72 hours
```

### Frontend → Spring Boot
```
http://localhost:8080/api/v1/...
Headers: Authorization: Bearer {JWT}
```

---

## 10. Status Enum State Transitions

```
┌──────────┐
│ SUBMITTED│
└─────┬────┘
      │ Bank verification triggered
      ▼
┌────────────────┐
│ BANK_VERIFYING │
└────────┬───────┘
         │ Bank confirms
         ▼
┌───────────┐
│ VERIFIED  │
└─────┬─────┘
      │ Fraud scoring triggered
      ▼
┌────────────┐
│  SCORED    │
└─────┬──────┘
      │ Risk assessment
      ├──────────────────┬──────────────────┐
      │                  │                  │
   (LOW)            (MEDIUM)             (HIGH)
      │                  │                  │
      ▼                  ▼                  ▼
┌──────────┐      ┌──────────────┐     ┌─────────┐
│ APPROVED │      │PENDING_REVIEW│     │ REJECTED│
└─────┬────┘      └──────────────┘     └─────────┘
      │
      │ Refund approved
      ▼
┌──────────────────┐
│ REFUND_PROCESSING│
└─────┬────────────┘
      │ NEFT completed
      ▼
┌─────────┐
│ REFUNDED│
└─────────┘
```

---

## Summary

This comprehensive diagram documentation covers:

1. ✅ System architecture with all components
2. ✅ Data flow from dispute submission to refund
3. ✅ API endpoints with request/response examples
4. ✅ Event-driven messaging topology (RabbitMQ)
5. ✅ Database schema with all tables
6. ✅ Deployment architecture (Railway.app)
7. ✅ Docker Compose setup for local development
8. ✅ Component communication details
9. ✅ Status state transitions

**Use these diagrams as reference during development!**
