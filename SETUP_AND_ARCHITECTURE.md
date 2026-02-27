# Setup & Architecture Guide
## Tech Stack, Design Decisions, and System Design

---

## Part 1: Why This Tech Stack?

### The Decision: Java + Spring Boot (Fintech-Optimized)

#### Why NOT Node.js?
```
Node.js Risk in Fintech:

❌ Float Precision Loss
   5000.10 + 0.10 = 5000.200000000001 (₹0.000000000001 lost!)

❌ No Compile-Time Type Safety
   service.createDispute(req)  // Crashes at 11pm in production

❌ No Built-in Transaction Safety
   INSERT → ERROR → Partial state (no automatic rollback)

❌ Less Regulated Trust
   Banks don't trust Node.js for money
```

#### Why Java + Spring Boot IS Optimal
```
✅ BigDecimal = Exact Money
   new BigDecimal("5000.10").add(new BigDecimal("0.10"))
   Result: 5000.20 (PERFECT)

✅ Compile-Time Safety
   DisputeService service = null;
   service.create(req);  // ❌ WON'T COMPILE

✅ @Transactional = ACID
   If any step fails, EVERYTHING rolls back (no partial states)

✅ Enterprise Proven
   ICICI, Stripe, PayPal, RBI systems = Java

✅ Thread-Safe by Default
   Spring handles concurrent requests safely
```

---

## Part 2: Complete Tech Stack

### Layer 1: Frontend
```
Framework:  React 18 + Vite
Styling:    Tailwind CSS
HTTP:       Axios with JWT interceptors
Charts:     Recharts (for admin dashboard)
State:      Redux (simple, predictable)

Why?
- Vite = 10x faster builds than Webpack
- React = Large ecosystem, components
- Tailwind = No CSS writing, responsive design
- Recharts = Financial visualizations built-in
- Axios = Simple JWT token management

Alternatives rejected:
- NextJS: Adds complexity, separate concerns is cleaner
- Vue: Smaller ecosystem
- Angular: Too much boilerplate
```

### Layer 2: Backend API
```
Language:   Java 17 (LTS)
Framework:  Spring Boot 3.2+
Speed:      Startup <5s (with optimizations)
ORM:        Spring Data JPA + Hibernate
Mapping:    MapStruct (auto-generate DTOs)
Utilities:  Lombok (cut boilerplate 50%)
Security:   Spring Security 6 + JWT + OAuth2
Logging:    SLF4J + Logback

Why Java 17?
- Latest LTS release (10-year support)
- Virtual threads (better concurrency)
- Records (less boilerplate)
- Better performance than older versions

Why Spring Boot 3?
- Latest version, active support
- Native compilation possible (faster startup)
- Better error messages
- Built-in micrometer monitoring

Optimizations for 24-hour hackathon:
1. Lombok @Data @Builder → -5 min/entity
2. MapStruct → -10 min/entity
3. Spring Data REST → -30 min (auto-generates CRUD)
4. Testcontainers → -1 hour (no Docker setup)
5. Spring Boot Starter (template) → -30 min (preset dependencies)
```

### Layer 3: ML/Fraud Service
```
Language:   Python 3.11
Framework:  FastAPI (async)
Algorithm:  scikit-learn Isolation Forest
Data Proc:  Pandas, NumPy
Explainability: SHAP (show why fraud score is X)
Startup:    <1 second

Why FastAPI?
- Async (handles 1K+ concurrent requests)
- Auto-generates OpenAPI docs
- Type hints native to Python
- 2x faster than Flask

Why Isolation Forest?
- Detects anomalies without labeled data (no training needed for MVP)
- Production-ready algorithm
- Fast scoring (<50ms per request)
- Explainable with SHAP

Why NOT Deep Learning?
- Requires GPU (expensive)
- Needs labeled training data (don't have)
- Overkill for hackathon (Isolation Forest is enough)
- Takes too long to train (24-hour sprint)
```

### Layer 4: Message Queue
```
Technology: RabbitMQ
Purpose:    Event-driven async processing
Pattern:    Publishers (Spring Boot) → Exchanges → Consumers (FastAPI, Spring Boot)

Why RabbitMQ?
✅ Industry standard for fintech
✅ Reliable message delivery (acknowledgements)
✅ AMQP protocol (bank-standard)
✅ Message persistence (survives restarts)
✅ Dead-letter queues (retry failed messages)

Why NOT Bull Queue (Node.js)?
- Redis-only (single point of failure)
- Less reliable than RabbitMQ
- Banks prefer RabbitMQ

Queue Topology:
- Exchange: "disputes" (topic-based routing)
  └─ Queue 1: "fraud-scoring-queue" (for ML service)
  └─ Queue 2: "refund-decision-queue" (for Spring Boot)
  └─ DLQ: "dead-letter-queue" (failed messages)

Events Published:
- disputes.created: New dispute submitted
- disputes.verified: Bank verification complete
- disputes.scored: Fraud score ready
- disputes.approved: Refund approved
- refunds.processing: Refund initiated
```

### Layer 5: Database
```
Technology: PostgreSQL 15
ORM:        Spring Data JPA + Hibernate
Migrations: Flyway (schema versioning)
Testing:    Testcontainers (auto Docker image)

Why PostgreSQL?
✅ ACID compliance (data consistency)
✅ Native JSON support (store SHAP explanations)
✅ Full-text search (search disputes)
✅ Triggers & stored procedures (complex logic)
✅ Mature (30+ years, battle-tested)
✅ Free and open-source

Why NOT MongoDB?
- Not ACID compliant (risky for money)
- No JOIN support (complex queries harder)
- Less suitable for relational financial data

Hosting: Supabase (PostgreSQL as a service)
- Free tier: 500MB DB, enough for hackathon
- Auto backups
- One-click deploy

Tables (6 total):
1. users (UPI IDs, KYC status)
2. disputes (status, amount, reason)
3. transactions (bank data, verification)
4. fraud_scores (ML results, SHAP values)
5. refunds (refund status, NEFT tracking)
6. audit_logs (compliance tracking)
```

### Layer 6: Cache
```
Technology: Redis
Purpose:    Speed up queries, idempotency, rate limiting
TTL:        Auto-expire old data

Use Cases:
1. Idempotency Keys
   Key: "dispute-{upi_tx_id}-{timestamp}"
   Value: dispute_id
   TTL: 24 hours
   → Prevents duplicate disputes if form submitted twice

2. JWT Blacklist
   Key: "blacklist-{token_hash}"
   Value: true
   TTL: Token expiration time
   → Logout by blacklisting JWT

3. Rate Limiting
   Key: "ratelimit-{user_id}-{hour}"
   Value: request_count
   TTL: 1 hour
   → Max 100 disputes/user/hour

4. Query Cache
   Key: "disputes-{page}-{size}"
   Value: paginated result
   TTL: 5 minutes
   → Cache admin dashboard queries

Bonus: Bull Queue integration
   Bull Queue stores job data in Redis
   Fault-tolerant message queue without separate RabbitMQ
   (But we use RabbitMQ for reliability)
```

### Layer 7: DevOps & Deployment
```
Containerization: Docker
Orchestration:    Docker Compose (local + staging)
Hosting:
  - Frontend: Vercel (optimized for React)
  - Backend: Railway (supports Java + PostgreSQL)
  - ML Service: Railway (supports Python)

Why Vercel?
- Built for React/Next.js
- Auto-deploy on git push
- Zero-config HTTPS + CDN
- Free tier: enough for hackathon

Why Railway?
- Supports multiple languages (Java, Python)
- Simple deployment (git push)
- Built-in PostgreSQL + Redis + RabbitMQ support
- Free tier: $5 credit (enough for 24 hours)

Deployment Steps:
1. Build Docker images locally: docker-compose build
2. Push to GitHub
3. Railway detects Dockerfile, auto-deploys
4. URL: https://yourname-backend.railway.app
```

---

## Part 3: System Architecture

### 3.1 High-Level Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    FRONTEND LAYER                       │
│             React 18 + Vite + Tailwind                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │ Submission   │  │ Tracking     │  │ Admin        │ │
│  │ Form         │  │ Dashboard    │  │ Dashboard    │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└──────────┬──────────────────────────────────────────────┘
           │ HTTP REST (JWT Auth)
           ▼
┌─────────────────────────────────────────────────────────┐
│              API GATEWAY / LOAD BALANCER               │
│            (Spring Boot embedded Tomcat)               │
└──────────┬──────────────────────────────────────────────┘
           │
    ┌──────┴──────────────────────┐
    │                             │
    ▼                             ▼
┌──────────────────┐      ┌──────────────────┐
│  Spring Boot 3   │      │  FastAPI         │
│  Dispute Engine  │      │  Fraud Service   │
├──────────────────┤      ├──────────────────┤
│ Controllers      │      │ /score endpoint  │
│ Services         │      │ ML model         │
│ Repositories     │      │ SHAP explainer   │
│ Event Handlers   │      │ Feature eng.     │
└────────┬─────────┘      └────────┬─────────┘
         │                         │
         │                         │
         └──────────────┬──────────┘
                        │
                        ▼
           ┌────────────────────────┐
           │   RabbitMQ Event Bus   │
           │   (AMQP protocol)      │
           │                        │
           │ Exchanges:             │
           │ • disputes (topic)     │
           │ • fraud (topic)        │
           │ • refunds (direct)     │
           │                        │
           │ Queues:                │
           │ • fraud-queue          │
           │ • refund-queue         │
           │ • notification-queue   │
           │ • dlq (dead-letter)    │
           └────────┬───────────────┘
                    │
       ┌────────────┼────────────┐
       │            │            │
       ▼            ▼            ▼
  ┌─────────┐  ┌─────────┐  ┌──────────┐
  │PostgreSQL   │ Redis   │  │ Email    │
  │Database     │ Cache   │  │ Service  │
  └─────────┘  └─────────┘  └──────────┘
```

### 3.2 Data Flow

```
Step 1: User Submission
─────────────────────────
User fills form on React frontend
├─ UPI TX ID: 314159265358979@upi
├─ Amount: ₹5000.50
├─ Reason: "Transaction failed"
└─ Click "Submit Dispute"

                    ↓

Step 2: Frontend Validation
─────────────────────────────
React validates:
├─ UPI format exists
├─ Amount > 0
└─ Reason not empty
If invalid → show error, stop
If valid → proceed

                    ↓

Step 3: API Call
─────────────────
HTTP POST /api/v1/disputes
Header: Authorization: Bearer {JWT_TOKEN}
Header: Idempotency-Key: {UUID} ← Prevents duplicates
Body: { upiTxId, amount, reason }

                    ↓

Step 4: Backend Processing
─────────────────────────────
Spring Boot Controller:
├─ Validate JWT token (Spring Security)
├─ Parse & validate request body
├─ Check Idempotency-Key in Redis
│  └─ If exists → return cached response (don't create again)
└─ Continue...

Spring Boot Service:
├─ Create Dispute entity
├─ Save to PostgreSQL (SUBMITTED status)
├─ Generate UUID disputeId
└─ Continue...

                    ↓

Step 5: Event Publishing
─────────────────────────
Spring Boot publishes to RabbitMQ:
├─ Exchange: disputes
├─ Routing key: disputes.created
├─ Payload: { disputeId, upiTxId, amount, userId }
└─ Continue...

                    ↓

Step 6: Bank Verification (Async)
──────────────────────────────────
FastAPI Mock Bank service listens on fraud-scoring-queue

Wait, actually it listens to disputes.created and calls bank:
Spring Boot calls → FastAPI Bank API
├─ Verifies transaction in bank system
├─ Checks UPI TX ID exists
├─ Returns: { verified: true, merchant: "XYZ" }
└─ Continue...

                    ↓

Step 7: Update & Publish
─────────────────────────
Spring Boot updates:
├─ Dispute status: VERIFIED
├─ Transaction details from bank
├─ Publish: disputes.verified event
└─ Continue...

                    ↓

Step 8: Fraud Scoring (Async)
──────────────────────────────
FastAPI Fraud Service listens on disputes.verified event

Triggers:
├─ Feature extraction:
│  ├─ Amount: ₹5000.50
│  ├─ Time of day: 2:32 PM
│  ├─ Customer history: 10 transactions last month
│  ├─ Device location: Same as usual
│  └─ etc...
├─ Isolation Forest model scores
├─ Returns: score 0.28 (LOW risk)
├─ Explains with SHAP: "Amount ok, time ok, user ok"
└─ Send back to Spring Boot via RabbitMQ

                    ↓

Step 9: Store Fraud Score
──────────────────────────
Spring Boot listens on disputes.scored event

Updates:
├─ FraudScore table: score=0.28, risk_level=LOW
├─ Publish: disputes.scored event
└─ Continue...

                    ↓

Step 10: Decision Engine
─────────────────────────
Spring Boot's DecisionService:
├─ Read fraud score: 0.28
├─ Apply logic:
│  ├─ If LOW (0-0.30) → APPROVED ✅
│  ├─ If MEDIUM (0.31-0.70) → PENDING_REVIEW ⏳
│  └─ If HIGH (0.71-1.0) → REJECTED ❌
├─ Score is LOW → set status to APPROVED
└─ Continue...

                    ↓

Step 11: Refund Processing
───────────────────────────
Spring Boot's RefundService:
├─ Create Refund record
├─ Initiate NEFT (bank transfer)
├─ Status: REFUND_PROCESSING
├─ Publish: refunds.processing event
└─ Continue...

                    ↓

Step 12: Frontend Updates (Real-time)
──────────────────────────────────────
React frontend polls every 30 seconds:
├─ GET /api/v1/disputes/{disputeId}
├─ Backend returns current status
└─ UI updates:
   Hour 0: "Dispute submitted ✓"
   Hour 0+5min: "Bank verifying..."
   Hour 0+10min: "Fraud check complete (Low risk)"
   Hour 0+12min: "APPROVED! Refund initiated ✓"
   Hour 1: "Money credited to your account ✓"

                    ↓

Step 13: Admin Dashboard
─────────────────────────
Admin views at http://admin.yourapp.com/dashboard:
├─ Total disputes: 45
├─ Approved: 43 (95%)
├─ Pending review: 1
├─ Rejected: 1
├─ Avg resolution time: 42 minutes
└─ Charts automatically update (Redis cached)
```

---

## Part 4: Database Schema (6 Tables)

### Table 1: users
```sql
CREATE TABLE users (
  id UUID PRIMARY KEY,
  upi_id VARCHAR(255) UNIQUE NOT NULL,  -- 314159265358979@upi
  phone VARCHAR(10) UNIQUE NOT NULL,
  kyc_status ENUM('PENDING', 'VERIFIED', 'REJECTED'),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

Indexes:
  - id (primary key, unique)
  - upi_id (for quick lookups)
  - phone (for customer verification)
```

### Table 2: disputes
```sql
CREATE TABLE disputes (
  id UUID PRIMARY KEY,
  user_id UUID FOREIGN KEY,
  upi_tx_id VARCHAR(255) UNIQUE,           -- 314159265358979@upi
  amount DECIMAL(15,2) NOT NULL,           -- ₹5000.50 (exact)
  reason VARCHAR(500),                      -- "Transaction failed"
  status ENUM('SUBMITTED', 'BANK_VERIFYING', 'VERIFIED', 
              'FRAUD_SCORING', 'SCORED', 'APPROVED', 'REJECTED',
              'PENDING_REVIEW', 'REFUND_PROCESSING', 'REFUNDED', 'FAILED'),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

Indexes:
  - id (primary key)
  - user_id (for user's disputes list)
  - status (for filtering in admin dashboard)
  - created_at DESC (for recent disputes first)
```

### Table 3: transactions
```sql
CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  dispute_id UUID FOREIGN KEY,
  bank_tx_id VARCHAR(255),  -- From bank verification
  merchant_id VARCHAR(255),  -- Who received money
  verification_status ENUM('PENDING', 'VERIFIED', 'FAILED'),
  verified_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

Indexes:
  - dispute_id (for transaction lookup)
  - bank_tx_id (for verification)
```

### Table 4: fraud_scores
```sql
CREATE TABLE fraud_scores (
  id UUID PRIMARY KEY,
  dispute_id UUID FOREIGN KEY,
  score DECIMAL(3,2),  -- 0.00 to 1.00
  risk_level ENUM('LOW', 'MEDIUM', 'HIGH'),
  shap_values JSONB,   -- {"amount": 0.05, "time": 0.08}
  explanation VARCHAR(500),  -- "Amount ok, time ok, user ok"
  created_at TIMESTAMP DEFAULT NOW()
);

Indexes:
  - dispute_id (for score lookup)
  - risk_level (for filtering HIGH-risk)
```

### Table 5: refunds
```sql
CREATE TABLE refunds (
  id UUID PRIMARY KEY,
  dispute_id UUID FOREIGN KEY,
  amount DECIMAL(15,2),
  status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'),
  neft_reference VARCHAR(255),  -- Bank refund ID
  processed_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT NOW()
);

Indexes:
  - dispute_id (for refund lookup)
  - status (for processing queue)
```

### Table 6: audit_logs
```sql
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  action VARCHAR(255),  -- "dispute_created", "fraud_scored"
  entity_type VARCHAR(50),  -- "Dispute", "Refund"
  entity_id UUID,
  old_value JSONB,
  new_value JSONB,
  changed_by UUID,  -- which service/user made change
  created_at TIMESTAMP DEFAULT NOW()
);

Indexes:
  - entity_id (for tracing one entity)
  - action (for compliance reporting)
  - created_at DESC (for recent changes)
```

---

## Part 5: API Endpoints (4 Core Endpoints)

### Endpoint 1: Create Dispute
```
POST /api/v1/disputes

Request Headers:
  Authorization: Bearer {JWT_TOKEN}
  Idempotency-Key: {UUID}  ← Prevents duplicates

Request Body:
{
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "reason": "Transaction failed, money not received"
}

Response (201 Created):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "status": "SUBMITTED",
  "createdAt": "2026-02-27T14:32:00Z"
}

Errors:
  400: Invalid format
  409: Duplicate (same UPI TX ID submitted before)
  401: Unauthorized (invalid JWT)
```

### Endpoint 2: Get Dispute Detail
```
GET /api/v1/disputes/{disputeId}

Response (200 OK):
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "upiTxId": "314159265358979@upi",
  "amount": 5000.50,
  "status": "APPROVED",
  "fraudScore": {
    "score": 0.28,
    "riskLevel": "LOW",
    "explanations": ["Amount ok", "Time ok", "User trusted"]
  },
  "refund": {
    "status": "PROCESSING",
    "neftReference": "12345NEFT"
  },
  "createdAt": "2026-02-27T14:32:00Z",
  "updatedAt": "2026-02-27T14:47:00Z"
}

Errors:
  404: Dispute not found
  401: Unauthorized
```

### Endpoint 3: List Disputes (with filters)
```
GET /api/v1/disputes?page=0&size=20&status=APPROVED&riskLevel=LOW

Response (200 OK):
{
  "content": [
    { ...dispute object... },
    { ...dispute object... }
  ],
  "totalElements": 45,
  "totalPages": 3,
  "currentPage": 0
}

Filters:
  - status: SUBMITTED, APPROVED, REJECTED, etc.
  - riskLevel: LOW, MEDIUM, HIGH
  - date range: createdAfter, createdBefore
```

### Endpoint 4: Get Timeline/History
```
GET /api/v1/disputes/{disputeId}/timeline

Response (200 OK):
[
  {
    "timestamp": "2026-02-27T14:32:00Z",
    "event": "dispute_created",
    "status": "SUBMITTED",
    "message": "Dispute submitted"
  },
  {
    "timestamp": "2026-02-27T14:33:00Z",
    "event": "bank_verified",
    "status": "VERIFIED",
    "message": "Bank confirmed transaction failure"
  },
  {
    "timestamp": "2026-02-27T14:37:00Z",
    "event": "fraud_scored",
    "status": "SCORED",
    "message": "Fraud risk: LOW (0.28)"
  },
  {
    "timestamp": "2026-02-27T14:38:00Z",
    "event": "dispute_approved",
    "status": "APPROVED",
    "message": "Auto-approved (low risk)"
  },
  {
    "timestamp": "2026-02-27T14:39:00Z",
    "event": "refund_initiated",
    "status": "REFUND_PROCESSING",
    "message": "NEFT initiated"
  }
]
```

---

## Part 6: Enums & Constants

### DisputeStatus (12 states)
```
SUBMITTED → BANK_VERIFYING → VERIFIED → FRAUD_SCORING →
SCORED → (APPROVED / REJECTED / PENDING_REVIEW) →
REFUND_PROCESSING → REFUNDED
```

### RiskLevel (from ML score)
```
LOW:     0.00 - 0.30  → Auto-approve
MEDIUM:  0.31 - 0.70  → Manual review
HIGH:    0.71 - 1.00  → Auto-reject
```

### Configuration (Database-Stored)
```
fraud_threshold_low = 0.30
fraud_threshold_medium = 0.70
auto_refund_enabled = true
max_refund_amount = 100000  (₹100k limit)
refund_timeout_hours = 24
```

---

## Part 7: Spring Boot Optimizations (For 24-Hour Sprint)

### Optimization 1: Lombok
Reduces boilerplate 50%:
```java
@Data            // Getters, setters
@Builder         // Builder pattern
@Entity
@Table(name = "disputes")
public class Dispute {
    @Id
    private UUID id;
    private String upiTxId;
    private BigDecimal amount;
    private DisputeStatus status;
    private LocalDateTime createdAt;
}
// That's it! No manual getters, setters, equals, toString
```

### Optimization 2: MapStruct
Auto-generates DTO mapping:
```java
@Mapper(componentModel = "spring")
public interface DisputeMapper {
    DisputeResponse toResponse(Dispute dispute);
}
// Generates 50 lines of mapping code automatically!
```

### Optimization 3: Spring Data REST
Auto-generates CRUD APIs:
```java
@RepositoryRestResource(path = "disputes")
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
}
// Automatically creates:
// GET    /api/disputes
// POST   /api/disputes
// GET    /api/disputes/{id}
// PUT    /api/disputes/{id}
// DELETE /api/disputes/{id}
```

### Optimization 4: Testcontainers
Auto-spins up Docker containers for testing:
```java
@Testcontainers
@SpringBootTest
public class DisputeServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static GenericContainer<?> rabbitmq =
        new GenericContainer<>("rabbitmq:3.12");
    
    // Tests run in Docker – no manual setup!
}
```

---

## Part 8: Checklist Before Coding

### Person A (Frontend + ML)
- [ ] Node.js 18+ installed
- [ ] Python 3.11+ installed
- [ ] React project created (npm create vite@latest)
- [ ] Tailwind CSS configured
- [ ] Axios setup for JWT
- [ ] FastAPI project created
- [ ] scikit-learn installed
- [ ] Redis running (docker-compose)

### Person B (Backend + Database)
- [ ] Java 17 JDK installed
- [ ] Maven 3.8+ installed
- [ ] Spring Boot starter project created (start.spring.io)
- [ ] PostgreSQL running (docker-compose)
- [ ] RabbitMQ running (docker-compose)
- [ ] application.yml configured
- [ ] Lombok IDE plugin installed
- [ ] Flyway migrations setup

---

## Summary

| Component | Technology | Why |
|-----------|-----------|-----|
| Frontend | React 18 + Vite | Fastest builds, large ecosystem |
| Backend | Java 17 + Spring 3 | Type-safe, fintech-proven, ACID |
| ML | Python + FastAPI | Best for ML, async API, fast |
| Queue | RabbitMQ | Fintech standard, reliable, AMQP |
| Database | PostgreSQL | ACID, mature, JSON support |
| Cache | Redis | Fast, perfect for idempotency |
| DevOps | Docker + Vercel + Railway | Easy deploy, scalable |

**Next: Read API_AND_DATABASE.md for all endpoint contracts and schema details.**
