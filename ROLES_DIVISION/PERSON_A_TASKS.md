# PERSON A - Core System Architect & Lead

## 👤 Role Overview

**Primary Responsibilities:**
- Overall system architecture and design
- Java Backend API development (REST endpoints)
- Database schema design & optimization
- Integration between all components
- Code review and quality assurance
- Deployment & DevOps setup

**Time Allocation (12 hours):**
- Setup & Architecture: 1 hour
- Java Backend Core: 4 hours
- Database & Optimization: 2 hours
- Integration & Testing: 2 hours
- Deployment & DevOps: 2 hours
- Final Review & Polish: 1 hour

---

## 📋 Detailed Tasks

### **Phase 1: Setup & Architecture (1 hour)**

**Tasks:**
- [ ] Set up Git repository structure
- [ ] Create Maven project with proper hierarchy
- [ ] Configure pom.xml with all dependencies
- [ ] Set up Spring Boot application.properties
- [ ] Create environment files (.env, .env.dev, .env.prod)
- [ ] Initialize database configuration (SQLite/PostgreSQL)
- [ ] Set up logging configuration (logback.xml)

**Deliverable:**
```
backend/
├── pom.xml
├── src/main/
│   ├── java/com/upi/
│   │   └── DemoApplication.java
│   └── resources/
│       ├── application.properties
│       └── application-dev.properties
└── disputes.db (auto-created)
```

---

### **Phase 2: Java Backend Core - REST API (4 hours)**

#### **2.1: Core Models & Entities (45 min)**

**Files to Create:**
- `src/main/java/com/upi/model/Dispute.java` - JPA Entity
- `src/main/java/com/upi/model/Merchant.java` - Optional merchant entity
- `src/main/java/com/upi/model/Transaction.java` - Transaction history

**Code Template:** See [06_IMPLEMENTATION_GUIDE.md](../06_IMPLEMENTATION_GUIDE.md)

**Checklist:**
- [ ] Dispute entity with all fields
- [ ] All annotations (@Entity, @Table, @Column)
- [ ] Timestamps (createdAt, updatedAt, verifiedAt, etc.)
- [ ] Status enum for dispute states
- [ ] Lombok for getters/setters

#### **2.2: Repository & DTOs (45 min)**

**Files to Create:**
- `src/main/java/com/upi/repository/DisputeRepository.java` - Spring Data JPA
- `src/main/java/com/upi/dto/DisputeRequest.java` - Request validation
- `src/main/java/com/upi/dto/DisputeResponse.java` - Response mapping

**Checklist:**
- [ ] JpaRepository interface
- [ ] Custom queries if needed (findByTransactionId, etc.)
- [ ] Request DTO with @Valid annotations
- [ ] Response DTO with all fields
- [ ] Error response DTO

#### **2.3: Business Logic Service (1 hour)**

**File to Create:**
- `src/main/java/com/upi/service/DisputeService.java` - Core business logic

**Logic Implementation:**
```java
fileDispute(DisputeRequest) {
  ├─ Validate input
  ├─ Check for duplicates
  ├─ Create dispute record
  ├─ Call bank verification APIs
  ├─ Decide: verified failure? false claim? manual review?
  ├─ Initiate NEFT refund if approved
  └─ Return response with dispute_id & neft_reference
}

getDisputeStatus(disputeId) {
  └─ Return current status with all details
}
```

**Checklist:**
- [ ] Duplicate transaction check
- [ ] Bank API verification (async/parallel)
- [ ] Decision logic (verified failure detection)
- [ ] NEFT refund initiation
- [ ] Error handling with fallback to manual review
- [ ] Proper exception handling

#### **2.4: REST Controller & Endpoints (1.5 hours)**

**File to Create:**
- `src/main/java/com/upi/controller/DisputeController.java`

**Endpoints to Implement:**

```
POST /api/disputes
├─ Input: DisputeRequest (transaction_id, merchant_upi, amount, phone)
├─ Process: fileDispute()
├─ Output: DisputeResponse (dispute_id, status, neft_reference)
└─ Validations: All fields required, format validation

GET /api/disputes/{disputeId}
├─ Input: Path param disputeId
├─ Process: getDisputeStatus()
├─ Output: Dispute entity with all details
└─ Error: 404 if not found

GET /api/health
├─ Output: {"status": "ok"}
└─ Purpose: Health check for deployment

GET /api/docs
└─ Swagger/OpenAPI documentation
```

**Checklist:**
- [ ] @RestController with @RequestMapping("/api/disputes")
- [ ] @PostMapping for file dispute
- [ ] @GetMapping for status check
- [ ] @CrossOrigin for frontend CORS
- [ ] Input validation (@Valid)
- [ ] Proper HTTP status codes (200, 400, 404, 422)
- [ ] Exception handling with error responses

---

### **Phase 3: Database & Optimization (2 hours)**

#### **3.1: Database Configuration**

**File to Create:**
- `src/main/java/com/upi/config/DatabaseConfig.java`

**Tasks:**
- [ ] Configure SQLite connection pooling (HikariCP)
- [ ] Set up Hibernate dialect for SQLite
- [ ] Configure DDL strategy (update vs. create)
- [ ] Add database indexes on frequently queried columns
- [ ] Set up transaction management

**Configuration:**
```properties
# HikariCP Connection Pooling
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=20000

# Hibernate Configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
```

#### **3.2: Database Optimization**

**Tasks:**
- [ ] Create indexes on status, transaction_id, created_at
- [ ] Set up query logging for performance monitoring
- [ ] Configure caching (optional)
- [ ] Backup strategy documentation

---

### **Phase 4: Integration & Testing (2 hours)**

#### **4.1: Integration Testing**

**Files to Create:**
- `src/test/java/com/upi/DisputeControllerTest.java`
- `src/test/java/com/upi/DisputeServiceTest.java`

**Test Cases to Cover:**
- [ ] File dispute with valid input → success
- [ ] File dispute with invalid input → validation error
- [ ] File dispute with duplicate transaction → error
- [ ] Get dispute status → returns correct data
- [ ] Verified failure scenario → refund initiated
- [ ] False claim scenario → rejected
- [ ] Unclear case → manual review

**Tech Stack:**
- JUnit 5
- Mockito for mocking bank APIs
- Spring Boot Test

#### **4.2: API Integration with Frontend**

**Tasks:**
- [ ] Start backend on port 8000
- [ ] Start frontend on port 5173
- [ ] Test CORS (should NOT see CORS errors)
- [ ] Test full flow: form submit → backend → response
- [ ] Test error cases in frontend
- [ ] Verify database persistence

**Test Endpoints with cURL:**
```bash
# File dispute
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{...}'

# Get status
curl http://localhost:8000/api/disputes/DIS_XXXX

# Health check
curl http://localhost:8000/api/health
```

---

### **Phase 5: Deployment & DevOps (2 hours)**

#### **5.1: Docker Setup**

**Files to Create:**
- `backend/Dockerfile`
- `docker-compose.yml` (in root)
- `.dockerignore`

**Dockerfile Content:**
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/dispute-api-1.0.0.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **5.2: Deployment Configuration**

**Tasks:**
- [ ] Package JAR: `mvn clean package`
- [ ] Create production properties file
- [ ] Set up environment variables for production
- [ ] Database migration scripts (Flyway/Liquibase)
- [ ] Health check setup
- [ ] Logging configuration for production

#### **5.3: CI/CD Setup** (Optional if time permits)

**Files:**
- `.github/workflows/build.yml` (GitHub Actions)
- Jenkins pipeline (alternative)

---

### **Phase 6: Final Review & Polish (1 hour)**

**Code Quality:**
- [ ] Run `mvn clean verify`
- [ ] All tests passing ✓
- [ ] No warnings or errors ✓
- [ ] Code follows Spring conventions ✓

**Documentation:**
- [ ] README.md with setup instructions ✓
- [ ] API documentation (Swagger) accessible ✓
- [ ] Database schema documented ✓
- [ ] Deployment guide written ✓

**Demo Readiness:**
- [ ] Backend responds to all API calls ✓
- [ ] Database persists data correctly ✓
- [ ] Error handling works smoothly ✓
- [ ] Performance acceptable (< 10 sec response) ✓

---

## 🎯 Success Criteria

**By End of 12 Hours:**

- ✅ Spring Boot application runs without errors
- ✅ All 3 API endpoints working (POST /disputes, GET /disputes/{id}, GET /health)
- ✅ Database schema created and indexed
- ✅ Valid requests return correct responses
- ✅ Invalid requests show proper error messages
- ✅ Bank verification logic working (simulated)
- ✅ Refund initiation working
- ✅ Frontend can successfully submit disputes
- ✅ Status tracking works end-to-end
- ✅ Docker image builds successfully
- ✅ All code tested and reviewed

---

## 📞 Communication with Team

**Checkpoints to Update Team:**

| Time | Status | Notes |
|------|--------|-------|
| Hour 1 | ✓ Setup complete | Backend project initialized, dependencies installed |
| Hour 2 | ✓ Models & DTOs | Database entities and request/response classes ready |
| Hour 3 | ✓ Service layer | Business logic implemented |
| Hour 4 | ✓ REST API | Controllers and endpoints working |
| Hour 5 | ✓ Integration | Backend integrated with frontend |
| Hour 6 | ✓ Testing | Unit and integration tests passing |
| Hour 8 | ✓ Deployment | Docker setup and packaging complete |
| Hour 12 | ✅ DONE | System ready for demo |

---

## 🛠️ Tools & Technologies Used

```
Build:      Maven, Java 17, Spring Boot 3.1
Database:   SQLite (dev), PostgreSQL (prod)
ORM:        Hibernate/Spring Data JPA
Testing:    JUnit 5, Mockito
Docker:     Docker, Docker Compose
Version:    Git + GitHub
IDE:        VS Code, IntelliJ IDEA (recommended)
```

---

## 📚 Reference Documents

- [04_API_CONTRACTS.md](../04_API_CONTRACTS.md) - API spec
- [05_TECH_STACK_AND_SETUP.md](../05_TECH_STACK_AND_SETUP.md) - Setup guide
- [06_IMPLEMENTATION_GUIDE.md](../06_IMPLEMENTATION_GUIDE.md) - Code templates
- [07_ROADMAP_AND_TIMELINE.md](../07_ROADMAP_AND_TIMELINE.md) - Timeline

**Good luck with development! 🚀**
