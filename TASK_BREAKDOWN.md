# 24-Hour Task Breakdown
## Hour-by-Hour Sprint Plan for 2-Person Team

---

## Team Division

**Person A**: Frontend (React) + ML Service (Python/FastAPI)
**Person B**: Backend API (Spring Boot) + Database + RabbitMQ

---

## PHASE 1: Setup & Infrastructure (Hours 0-2)

### Person A Setup (Hour 0-1)
- [ ] Clone repository from GitHub
- [ ] Install Node.js 18+ + npm
- [ ] Install Python 3.11+
- [ ] `npm create vite@latest frontend -- --template react`
- [ ] Install dependencies: `npm install axios redux recharts`
- [ ] Setup Tailwind CSS: `npm install -D tailwindcss`
- [ ] Test dev server: `npm run dev` (should run on localhost:5173)
- [ ] Create `.env` for API_BASE_URL (backend URL)

**Deliverable**: React dev server running with hot reload

### Person B Setup (Hour 0-1)
- [ ] Clone repository
- [ ] Install Java 17 JDK
- [ ] Install Maven 3.8+
- [ ] Go to start.spring.io, create project with:
  - Spring Boot 3.2.x
  - Spring Web, Spring Data JPA, Spring Security
  - PostgreSQL Driver, Lombok, MapStruct
  - Spring Cloud Stream (config for RabbitMQ)
- [ ] Download ZIP and extract
- [ ] Test Maven build: `mvn clean install`
- [ ] Create `application-dev.yml` for local config

**Deliverable**: Spring Boot scaffolding ready

### Both Together (Hour 1-2)
- [ ] Create `docker-compose.yml` in root:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: upi_dispute
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    
  redis:
    image: redis:7
    ports:
      - "6379:6379"
  
  rabbitmq:
    image: rabbitmq:3.12-management
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: password
    ports:
      - "5672:5672"
      - "15672:15672"  # admin console
```

- [ ] Run: `docker-compose up`
- [ ] Verify all services up: postgres, redis, rabbitmq
- [ ] Test RabbitMQ console: http://localhost:15672
- [ ] Test PostgreSQL connection: `psql -U admin -d upi_dispute`
- [ ] Create Git branches: `git checkout -b feature/frontend` and `feature/backend`

**Deliverable**: All infrastructure running, ready for development

---

## PHASE 2: Core Development (Hours 2-8)

### Person A: Component Structure (Hour 2-4)

**Task 1: Create React Pages**
- [ ] Create `src/pages/DisputeSubmitPage.jsx`
  - Form: UPI TX ID, Amount, Reason
  - Input validation
  - Submit button + error messages
- [ ] Create `src/pages/DisputeTrackingPage.jsx`
  - Display list of disputes
  - Click to view detail
  - Real-time status updates
- [ ] Create `src/pages/AdminDashboardPage.jsx`
  - 4 charts (status pie, timeline line, risk histogram, reasons bar)
  - Key metrics: total, approved %, avg time
- [ ] Create `src/pages/LoginPage.jsx` (mock JWT login)

**Task 2: Create Reusable Components**
- [ ] `src/components/DisputeForm.jsx` - Form component
- [ ] `src/components/DisputeCard.jsx` - Dispute list item
- [ ] `src/components/DisputeStatusTimeline.jsx` - Status tracker
- [ ] `src/components/Header.jsx` - Navigation
- [ ] `src/components/Modal.jsx` - Reusable modal

**Task 3: Create Chart Components**
- [ ] `src/components/RiskDistributionChart.jsx` (Recharts)
- [ ] `src/components/ResolutionTimeChart.jsx` (Recharts)
- [ ] `src/components/FraudReasonChart.jsx` (Recharts)

**Task 4: Styling with Tailwind**
- [ ] Add Tailwind config to `tailwind.config.js`
- [ ] Style all pages & components
- [ ] Ensure responsive design (mobile 320px+)
- [ ] Add dark mode support (optional)

**Deliverable**: All UI components render without errors, styled with Tailwind

---

### Person B: Database & API (Hour 2-4)

**Task 1: Create Database Schema**
- [ ] Write Flyway migration: `V1__initial_schema.sql`
  - Create users, disputes, transactions, fraud_scores, refunds, audit_logs tables
  - Add indexes, foreign keys, constraints
- [ ] Place in `src/main/resources/db/migration/`
- [ ] Run: `mvn spring-boot:run` (should auto-apply migration)
- [ ] Verify tables created in PostgreSQL: `\dt`

**Task 2: Create JPA Entities (with Lombok)**
- [ ] `src/main/java/com/upi/entity/User.java`
  - Fields: id, upiId, phone, kycStatus
  - Annotations: @Data, @Entity, @Builder
- [ ] `src/main/java/com/upi/entity/Dispute.java`
  - Fields: id, userId, upiTxId, amount, reason, status
  - @Data, @Entity, @Builder
- [ ] `src/main/java/com/upi/entity/FraudScore.java`
  - Fields: id, disputeId, score, riskLevel, shapValues
- [ ] `src/main/java/com/upi/entity/Refund.java`
  - Fields: id, disputeId, amount, status, neftReference
- [ ] Add @NoArgsConstructor, @AllArgsConstructor where needed

**Task 3: Create Spring Data Repositories**
- [ ] `src/main/java/com/upi/repository/DisputeRepository.java`
  - Extend JpaRepository<Dispute, UUID>
  - Custom methods: findByStatus, findByUpiTxId
- [ ] `src/main/java/com/upi/repository/FraudScoreRepository.java`
- [ ] `src/main/java/com/upi/repository/RefundRepository.java`

**Task 4: Create DTOs & Mappers (with MapStruct)**
- [ ] `src/main/java/com/upi/dto/DisputeRequest.java`
  - Fields: upiTxId, amount, reason
- [ ] `src/main/java/com/upi/dto/DisputeResponse.java`
  - Fields: id, upiTxId, amount, status, createdAt
- [ ] `src/main/java/com/upi/mapper/DisputeMapper.java`
  - @Mapper annotation
  - Method: DisputeResponse toResponse(Dispute)

**Deliverable**: DB schema working, entities created, DTOs mapped

---

### Person A: API Integration (Hour 4-6)

**Task 1: Create Axios Service**
- [ ] `src/services/apiClient.js`
  - New axios instance
  - Base URL from .env
  - Add JWT token to headers
  - Handle 401 (token refresh)
  - Add error handling (show toast)

**Task 2: Create API Hooks**
- [ ] `src/hooks/useDisputeList.js`
  - Fetch GET /api/v1/disputes
  - Handle loading, error, data
  - Return {disputes, loading, error}
- [ ] `src/hooks/useDisputeDetail.js`
  - Fetch GET /api/v1/disputes/{id}
- [ ] `src/hooks/useAuth.js`
  - Login, logout, token management
- [ ] `src/hooks/usePoll.js`
  - Poll API every 30 seconds
  - Update data automatically

**Task 3: Setup Redux (Simple)**
- [ ] `src/store/slices/disputeSlice.js`
  - State: disputes, selectedDispute, loading
  - Actions: setDisputes, selectDispute, setLoading
- [ ] `src/store/index.js` - Configure store
- [ ] Connect to pages using useSelector, useDispatch

**Task 4: Integrate Form**
- [ ] Connect DisputeSubmitPage form to API
  - On submit: POST /api/v1/disputes
  - Show loading spinner
  - On success: navigate to tracking page
  - On error: show error toast

**Deliverable**: Frontend can call backend APIs (even if backend not complete)

---

### Person B: REST Controllers & Services (Hour 4-6)

**Task 1: Create Service Layer**
- [ ] `src/main/java/com/upi/service/DisputeService.java`
  - Method: createDispute(DisputeRequest) → DisputeResponse
    - Validate input
    - Create entity
    - Save to DB
    - Publish event to RabbitMQ (later)
  - Method: getDispute(UUID) → DisputeResponse
  - Method: listDisputes(page, size, status) → Page<DisputeResponse>

**Task 2: Create Controllers**
- [ ] `src/main/java/com/upi/controller/DisputeController.java`
  - @RestController @RequestMapping("/api/v1/disputes")
  - POST /disputes → createDispute()
  - GET /{id} → getDispute()
  - GET → listDisputes()

**Task 3: Add Global Exception Handling**
- [ ] `src/main/java/com/upi/exception/GlobalExceptionHandler.java`
  - @ControllerAdvice
  - @ExceptionHandler for common errors
  - Return standard error response (400, 404, 409, 500)

**Task 4: Add Input Validation**
- [ ] Use @Valid, @NotNull, @DecimalMin in DTOs
- [ ] Custom validators for UPI format
- [ ] Return 400 with field errors

**Deliverable**: All 4 API endpoints returning mock responses (201, 200, 404, 400)

---

### Person A: Dashboard & Real-Time (Hour 6-8)

**Task 1: Build Admin Dashboard**
- [ ] Create AdminDashboardPage layout
- [ ] Fetch GET /api/v1/admin/analytics (mock data)
- [ ] Display 4 charts with Recharts
- [ ] Add filters: date, status, risk level
- [ ] Display key metrics

**Task 2: Real-Time Status Updates**
- [ ] Implement usePoll hook
- [ ] Poll GET /api/v1/disputes/{id} every 30s
- [ ] Update UI when status changes
- [ ] Animate status transitions

**Task 3: Timeline Component**
- [ ] Render StatusTimeline component
- [ ] Show: created, verifying, verified, scored, approved, refunding, refunded
- [ ] Show timestamps for each

**Deliverable**: Dashboard displays data, real-time updates working

---

### Person B: RabbitMQ & Events (Hour 6-8)

**Task 1: Configure RabbitMQ**
- [ ] `src/main/java/com/upi/config/RabbitMQConfig.java`
  - Create topic exchange: "disputes"
  - Create queues: "fraud-scoring-queue", "refund-decision-queue"
  - Create bindings with routing keys

**Task 2: Publish Events**
- [ ] In DisputeService.createDispute():
  - After saving: rabbitTemplate.convertAndSend("disputes", "disputes.created", dispute)
- [ ] Use RabbitTemplate from Spring

**Task 3: Create Event Listeners**
- [ ] `src/main/java/com/upi/listener/DisputeEventListener.java`
  - @RabbitListener(queues = "fraud-scoring-queue")
  - Print received events to console (log)
  - Update dispute status to BANK_VERIFYING

**Deliverable**: Events publishing to RabbitMQ, listeners receiving & logging

---

## PHASE 3: Integration & Testing (Hours 8-12)

### Integration Test 1: Dispute Creation (Hour 8-9)

**Person A:**
- [ ] Test DisputeSubmitPage form
- [ ] Fill form and click submit
- [ ] Should POST /api/v1/disputes
- [ ] Should show success message
- [ ] Should navigate to tracking page

**Person B:**
- [ ] Verify POST endpoint receives request
- [ ] Check dispute saved in database
- [ ] Check event published to RabbitMQ

**Both:**
- [ ] Create 1 test dispute end-to-end
- [ ] Sync code: `git merge feature/backend origin/main`
- [ ] Test together on same developer machine

**Deliverable**: Full dispute creation flow works end-to-end

---

### Integration Test 2: ML Fraud Service (Hour 9-10)

**Person A:**
- [ ] Create FastAPI service in `fraud-service/` folder
  - `main.py` with FastAPI app
  - POST /api/v1/fraud-score endpoint
  - Request: disputeId, amount, userName, etc.
  - Response: {score: 0.28, riskLevel: "LOW", reasons: [...], shapValues: {...}}

- [ ] For MVP: Return mock scores (no real ML)
  ```python
  @app.post("/api/v1/fraud-score")
  async def score_fraud(request: FraudScoreRequest):
      # Mock low-risk score
      return {
          "score": 0.28,
          "riskLevel": "LOW",
          "reasons": ["Amount ok", "Time ok"],
          "shapValues": {"amount": 0.05, "time": 0.08}
      }
  ```

**Person B:**
- [ ] In DisputeEventListener, call fraud service
  - Use RestTemplate or WebClient
  - POST to http://localhost:8001/api/v1/fraud-score
  - Save response as FraudScore entity
  - Publish event: disputes.scored

**Both:**
- [ ] Verify fraud score saved to database
- [ ] Verify events published

**Deliverable**: ML service scoring disputes, scores saved to DB

---

### Integration Test 3: Auto-Refund Decision (Hour 10-11)

**Person B:**
- [ ] Create RefundDecisionHandler
- [ ] Listen to disputes.scored events
- [ ] Check fraud score:
  - If LOW → Set status to APPROVED
  - If MEDIUM → Set status to PENDING_REVIEW
  - If HIGH → Set status to REJECTED
- [ ] Create Refund record if APPROVED
- [ ] Save to database

**Person A:**
- [ ] Frontend polling should detect status change
- [ ] Show approval message
- [ ] Update timeline

**Both:**
- [ ] Create low-risk dispute, verify auto-approval
- [ ] Create test dispute with MEDIUM score, verify pending review
- [ ] Check database dispute.status changed

**Deliverable**: Decision engine working, disputes auto-approved/rejected

---

### Integration Test 4: Admin Dashboard (Hour 11-12)

**Person B:**
- [ ] Create AdminController endpoint
  - GET /api/v1/admin/analytics
  - Return: total count, approved %, avg resolution time, etc.
  - Can be hardcoded for now

**Person A:**
- [ ] AdminDashboardPage polling /api/v1/admin/analytics
- [ ] Charts update with data
- [ ] Verify correct metrics shown

**Both:**
- [ ] Create 5 test disputes
- [ ] Check dashboard shows all 5
- [ ] Check metrics calculated correctly

**Deliverable**: Dashboard fully functional, showing metrics

---

## PHASE 4: Testing & Bug Fixes (Hours 12-20)

### Person A: Frontend Testing (Hour 12-16)

**Hour 12-13: Unit Tests**
- [ ] Install Jest + React Testing Library: `npm install --save-dev jest @testing-library/react`
- [ ] Write tests for DisputeForm component
  - Test input validation
  - Test form submission
- [ ] Write tests for useDisputeList hook
- [ ] Run: `npm run test`

**Hour 13-14: Integration Tests**
- [ ] Test form submit → API call → list updates
- [ ] Test error handling shows toast
- [ ] Test pagination works

**Hour 14-15: Manual E2E**
- [ ] Create dispute from form
- [ ] See it in list
- [ ] Click detail, see timeline
- [ ] Go to dashboard, see metrics

**Hour 15-16: Polish**
- [ ] Fix responsive design issues
- [ ] Improve error messages
- [ ] Add loading spinners
- [ ] Test on Chrome, Firefox (mobile view)
- [ ] Fix any accessibility issues

**Deliverable**: Frontend fully tested, no console errors

---

### Person B: Backend Testing (Hour 12-16)

**Hour 12-13: Unit Tests**
- [ ] Create test class: `src/test/java/com/upi/service/DisputeServiceTest.java`
  - Test createDispute method
  - Test exception handling
  - Mock repositories
- [ ] Create test class for repositories
- [ ] Run: `mvn test`

**Hour 13-14: Integration Tests**
- [ ] Use Testcontainers to auto-boot PostgreSQL + RabbitMQ
- [ ] Test full dispute flow: create → save → event published
- [ ] Verify database state changes

**Hour 14-15: API Testing (Postman)**
- [ ] POST /api/v1/disputes → 201
- [ ] GET /api/v1/disputes/{id} → 200
- [ ] GET /api/v1/disputes → 200 with pagination
- [ ] GET /api/v1/admin/analytics → 200
- [ ] Error cases: 400, 404, 409

**Hour 15-16: Load Testing (Optional)**
- [ ] 100 concurrent POST /disputes
- [ ] Check DB performance
- [ ] Check RabbitMQ queue not accumulating

**Deliverable**: All tests passing, API responds correctly

---

### Both: Bug Fixes & Edge Cases (Hour 16-20)

**Hour 16-17: Test Edge Cases**
- [ ] Duplicate dispute (same UPI TX ID) → 409 error
- [ ] Invalid UPI format → 400 error
- [ ] Non-existent dispute ID → 404 error
- [ ] Missing JWT token → 401 error
- [ ] High amounts (₹999,999) → handled correctly
- [ ] Low amounts (₹1) → handled correctly

**Hour 17-18: Bug Fixes**
- [ ] Fix UI rendering issues
- [ ] Fix API response formats
- [ ] Fix timeout issues
- [ ] Fix race conditions (if any)

**Hour 18-19: Performance Optimization**
- [ ] Add indexes to slow queries
- [ ] Cache admin analytics (5-minute TTL in Redis)
- [ ] Verify API response time <100ms

**Hour 19-20: Final QA**
- [ ] Create 10 test disputes
- [ ] Verify all features work
- [ ] Verify responsive on mobile
- [ ] Verify no errors in console
- [ ] Verify no errors in logs

**Deliverable**: Production-ready code, all bugs fixed

---

## PHASE 5: Deployment & Demo (Hours 20-24)

### Hour 20-21: Dockerize & Deploy

**Person B (Lead):**
- [ ] Create Dockerfile for Spring Boot
  ```dockerfile
  FROM openjdk:17-jdk-slim
  COPY target/app.jar app.jar
  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```
- [ ] Create Dockerfile for FastAPI
  ```dockerfile
  FROM python:3.11-slim
  COPY requirements.txt .
  RUN pip install -r requirements.txt
  COPY . .
  CMD ["uvicorn", "main:app", "--host", "0.0.0.0"]
  ```
- [ ] Test Docker builds: `docker-compose build`
- [ ] Deploy to Railway:
  - Push to GitHub
  - Connect Railway to GitHub repo
  - Set environment variables (DATABASE_URL, etc.)
  - Deploy (auto-builds + deploys Docker images)

**Person A:**
- [ ] Deploy frontend to Vercel:
  - Connect GitHub repo to Vercel
  - Auto-detects React/Vite
  - Sets environment variables (API_BASE_URL=production)
  - Deploy (auto-builds + deploys)

**Both:**
- [ ] Update frontend .env with production API URL
- [ ] Test live URLs work

**Deliverable**: Live app running on production URLs

---

### Hour 21-22: Live Testing

**Both:**
- [ ] Test on live server
  - Create dispute on production
  - Verify appears in list
  - Verify fraud scoring works
  - Verify auto-approval works
  - Verify dashboard shows metrics
- [ ] Take screenshots for demo

**Sanity Checks:**
- [ ] Frontend loads without errors
- [ ] All pages accessible
- [ ] All API endpoints respond
- [ ] Dashboard shows correct metrics
- [ ] No JS errors in console
- [ ] No server errors in logs

**Deliverable**: Live app fully functional

---

### Hour 22-24: Demo & Submission

**Person A: Demo Script (5 minutes)**
```
1. Show problem (30 seconds):
   "UPI disputes take 5-7 days to resolve. That's unacceptable."

2. Show solution (30 seconds):
   Go to live app → Submission form
   "Our system resolves disputes in 2-4 hours."

3. Demo dispute creation (1 minute):
   Fill form with sample data
   Click submit
   Show success message
   Navigate to tracking page
   Explain: Bank is verifying, ML is scoring, decision pending

4. Show tracking in real-time (1 minute):
   Show status timeline
   "Notice status updates in real-time"
   Explain each step: submitted → verified → scored → approved → refunded

5. Show admin dashboard (1 minute):
   Admin dashboard with 4 charts
   "Our fraud detection is 95% accurate"
   "We approve 95% of low-risk disputes automatically"

6. Key message (30 seconds):
   "5-7 days → 2-4 hours"
   "Manual process → 95% automated"
   "Zero fraud-related refunds"
```

**Person B: Tech Stack Explanation**
- Briefly explain architecture
- Mention fraud AI model
- Highlight enterprise-grade tech (Java, PostgreSQL)

**Presentation Tips:**
- Practice presentation 3 times before demo
- Have backup test disputes pre-created
- Have phone ready to show mobile UI
- If live demo fails: show pre-recorded video
- Rehearse answers to common questions:
  - "How do you prevent fraud?" → ML detection
  - "What about false positives?" → Manual review queue
  - "How does it scale?" → Event-driven, async processing

**Deliverable**: 5-minute live demo, impressive presentation

---

## Daily Sync Schedule

| Hour | Duration | Topics | Attendees |
|------|----------|--------|-----------|
| 4 | 15 min | Setup complete? Any blockers? | Both |
| 8 | 15 min | API integration working? DB queries ok? | Both |
| 12 | 20 min | E2E flow working? All tests passing? | Both |
| 18 | 15 min | Testing status? Remaining bugs? | Both |
| 23 | 10 min | Final checks before submission | Both |

---

## Success Checklist (Final Delivery)

Critical (MUST HAVE):
- [ ] Dispute creation form works (POST /api/v1/disputes → 201)
- [ ] Disputes appear in list (GET /api/v1/disputes → 200)
- [ ] Status updates in real-time (frontend polling works)
- [ ] Fraud ML service scores disputes
- [ ] Low-risk disputes auto-approve
- [ ] High-risk disputes auto-reject
- [ ] Admin dashboard shows 4 charts
- [ ] Database has all 6 tables with correct data
- [ ] RabbitMQ events publishing
- [ ] Responsive design (mobile + desktop)
- [ ] Deployed on Railway (live URL)
- [ ] Demo script rehearsed

Nice-to-Have (OPTIONAL):
- [ ] Unit test coverage >80%
- [ ] Load test (100+ concurrent users)
- [ ] Dark mode UI
- [ ] Email notifications
- [ ] SMS notifications
- [ ] Manual review queue UI

---

## Time Management Tips

1. **Every 2 Hours**: Commit to Git
   ```bash
   git add .
   git commit -m "Hour X: [feature name]"
   git push origin feature/branch
   ```

2. **Every 4 Hours**: Sync & Merge
   - Review Person A's work + Person B's work
   - Merge branches to main (or test)
   - Resolve conflicts if any

3. **If Stuck**: 
   - Ask the other person (pair programming)
   - Skip that feature, move to next
   - Don't spend >30 min on one bug

4. **Prioritize**:
   - Core flow (1-2-3-4) is CRITICAL
   - Testing (4) is nice-to-have
   - Polish (4) is optional

5. **Day-Of Schedule**:
   - 0-2h: Setup (mandatory)
   - 2-8h: Building (fast pace)
   - 8-12h: Integration (careful)
   - 12-20h: Testing (thorough)
   - 20-24h: Deploy + demo (confident)

---

**Ready to code? Start Hour 0 now! 🚀**

**Remember: Working software beats perfect features. Finish MVP first, then polish.**
