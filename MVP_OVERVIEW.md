# 🚀 UPI Dispute Resolution - MVP Overview

## 📋 What is MVP?

**Minimum Viable Product (MVP)** = Core functionality only

```
✅ File dispute via form
✅ Auto-verify with mock bank API
✅ Store in SQLite database
✅ Show refund status instantly
✅ Track NEFT reference

❌ ML risk scoring (Phase 2)
❌ Merchant dashboard (Phase 2)
❌ Advanced features (Phase 2)
```

---

## 👥 MVP Team (2 People)

### **Person A - Backend Developer** ⏱️ 12 hours

**What to build:**
- ✅ Spring Boot REST API
- ✅ SQLite database configuration
- ✅ Mock Bank API (built-in)
- ✅ 3 REST endpoints
  - `POST /api/disputes` - File new dispute
  - `GET /api/disputes/{id}` - Get status
  - `GET /api/health` - Health check

**Key Responsibilities:**
1. **Hour 1-2:** Setup Spring Boot project, Maven, database
2. **Hour 3-8:** Create Java classes (Dispute entity, Service, Controller, DTOs)
3. **Hour 9-10:** Mock bank API (simulated verification)
4. **Hour 11:** Testing and validation
5. **Hour 12:** Docker testing

**Tools:**
- Java 17
- Spring Boot 3.1
- Maven
- SQLite
- Postman/cURL for testing

---

### **Person B1 - Frontend Developer** ⏱️ 12 hours

**What to build:**
- ✅ React application
- ✅ Dispute filing form
- ✅ Status page
- ✅ Responsive design

**Key Responsibilities:**
1. **Hour 1-2:** Setup React Vite project, dependencies
2. **Hour 3-4:** DisputeForm component with validation
3. **Hour 5-6:** StatusPage component
4. **Hour 7-8:** API integration (axios)
5. **Hour 9-10:** Styling and responsiveness
6. **Hour 11:** Testing and bug fixes
7. **Hour 12:** Polish and verification

**Tools:**
- React 18
- Vite
- Tailwind CSS
- Axios
- Browser DevTools for testing

---

## 📊 MVP Feature Set

### **Core Features**
```
1. Dispute Filing
   ├─ Transaction ID input
   ├─ Merchant UPI input
   ├─ Amount input
   ├─ Phone number input
   └─ Submit button

2. Bank Verification (Mock)
   ├─ Simulated API call
   ├─ ~2 second response time
   ├─ Returns status (approved/verified)
   └─ Generates NEFT reference

3. Status Tracking
   ├─ Show dispute ID
   ├─ Show current status
   ├─ Show NEFT reference
   ├─ Show expected settlement time
   └─ Copy-to-clipboard buttons

4. Database Storage
   ├─ All disputes saved to SQLite
   ├─ Status persisted
   ├─ NEFT reference stored
   └─ Timestamps recorded
```

---

## 🗄️ Database Schema (SQLite)

```sql
CREATE TABLE disputes (
    id              VARCHAR(20) PRIMARY KEY,      -- DIS_1709028600
    transaction_id  VARCHAR(50) NOT NULL,        -- TXN20260227123456
    merchant_upi    VARCHAR(50) NOT NULL,        -- amazon@upi
    amount          DECIMAL(10, 2) NOT NULL,     -- 5000.00
    customer_phone  VARCHAR(15) NOT NULL,        -- +919876543210
    status          VARCHAR(20) NOT NULL,        -- REFUND_INITIATED
    neft_reference  VARCHAR(50),                 -- NEFT000123456789
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);
```

---

## 📡 API Contracts (3 Endpoints)

### **1. File Dispute**
```bash
POST /api/disputes
Content-Type: application/json

REQUEST:
{
  "transactionId": "TXN20260227123456",
  "merchantUpi": "amazon@upi",
  "amount": 5000,
  "customerPhone": "+919876543210"
}

RESPONSE (201):
{
  "disputeId": "DIS_1709028600",
  "status": "REFUND_INITIATED",
  "neftReference": "NEFT000123456789",
  "message": "Dispute filed successfully"
}
```

### **2. Get Dispute Status**
```bash
GET /api/disputes/{disputeId}

RESPONSE (200):
{
  "disputeId": "DIS_1709028600",
  "transactionId": "TXN20260227123456",
  "merchantUpi": "amazon@upi",
  "amount": 5000,
  "status": "REFUND_INITIATED",
  "neftReference": "NEFT000123456789",
  "createdAt": "2025-02-27T14:30:00Z",
  "expectedSettlement": "2025-02-28T14:30:00Z"
}
```

### **3. Health Check**
```bash
GET /api/health

RESPONSE (200):
{
  "status": "UP",
  "timestamp": "2025-02-27T14:35:00Z"
}
```

---

## 🐳 Docker Setup (MVP Only)

```bash
# Start MVP (backend + frontend, no ML)
docker-compose -f docker-compose.yml up -d --remove-orphans

# Or remove ML service from docker-compose.yml
# Services:
#   - backend  (port 8000)
#   - frontend (port 5173)
#   - Skipped: ml-service
```

---

## 📅 Timeline

### **Day 1 - Person A (Backend)**
```
Hour 1-2:   Setup Spring Boot, Maven, SQLite
Hour 3-4:   Create Dispute entity and repository
Hour 5-6:   Create Service layer with business logic
Hour 7-8:   Create Controller with 3 endpoints
Hour 9:     Mock bank API integration
Hour 10:    Testing with Postman/cURL
Hour 11:    Bug fixes and validation
Hour 12:    Docker build and verify
✅ Done: Backend running on port 8000
```

### **Day 2 - Person B1 (Frontend)**
```
Hour 1-2:   Setup React Vite, Tailwind CSS
Hour 3:     DisputeForm component
Hour 4:     Form validation logic
Hour 5-6:   StatusPage component
Hour 7:     Axios API integration
Hour 8:     Styling and layout
Hour 9:     Responsive design (mobile)
Hour 10:    Testing integration
Hour 11:    Polish and bug fixes
Hour 12:    Final verification
✅ Done: Frontend running on port 5173
```

### **Day 3 - Both (Integration)**
```
Hour 1:     Test end-to-end flow
Hour 2:     Fix any integration issues
Hour 3:     Final testing
Hour 4:     Deploy to docker-compose
✅ Done: MVP complete and working
```

---

## 🎯 MVP Success Criteria

**By end of development, all should be TRUE:**

```javascript
✅ Backend starts without errors
✅ Frontend starts without errors
✅ Form submits successfully to backend
✅ Dispute data saved to SQLite
✅ Status endpoint returns correct data
✅ NEFT reference is unique and generated
✅ Frontend can retrieve and display status
✅ Responsive design works on mobile
✅ All 3 API endpoints working
✅ No CORS errors
✅ No console errors or warnings
✅ Health check returns UP
✅ Docker containers run without issues
```

---

## 🚀 Running the MVP

### **Quick Start**
```bash
# 1. Clone repo
git clone <repo-url>
cd upi-dispute-resolution

# 2. Setup environment
cp .env.example .env.dev

# 3. Start services
docker-compose up -d

# 4. Verify
curl http://localhost:8000/api/health
# Should return: { "status": "UP" }

# 5. Open frontend
open http://localhost:5173
```

### **Test the Flow**
```bash
# 1. File a dispute (backend)
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN20260227123456",
    "merchantUpi": "amazon@upi",
    "amount": 5000,
    "customerPhone": "+919876543210"
  }'

# Response will give you: disputeId, status, neftReference

# 2. Get status (use disputeId from response)
curl http://localhost:8000/api/disputes/DIS_1709028600

# 3. Or use frontend form and see status display
```

---

## 🛠️ Tech Stack (MVP)

| Component | Technology | Port |
|-----------|-----------|------|
| **Backend** | Java 17 + Spring Boot 3.1 | 8000 |
| **Frontend** | React 18 + Vite | 5173 |
| **Database** | SQLite (dev) | - |
| **Build** | Maven (Java), npm (React) | - |
| **Container** | Docker + Docker Compose | - |

**No ML, no extra services, just what's needed.**

---

## 📝 File Structure (MVP)

```
upi-dispute-resolution/
├── backend/
│   ├── src/main/java/com/upi/
│   │   ├── model/Dispute.java
│   │   ├── repo/DisputeRepository.java
│   │   ├── service/DisputeService.java
│   │   ├── controller/DisputeController.java
│   │   ├── dto/DisputeRequest.java
│   │   ├── dto/DisputeResponse.java
│   │   └── DemoApplication.java
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/DisputeForm.jsx
│   │   ├── components/StatusPage.jsx
│   │   ├── App.jsx
│   │   └── main.jsx
│   └── package.json
│
├── docker-compose.yml (backend + frontend only)
├── .env.dev
└── README.md
```

---

## ⚠️ MVP Limitations

**What won't be in MVP:**

```
❌ Machine Learning risk scoring
❌ Merchant dashboard
❌ Auto-prevention recommendations
❌ Chatbot support
❌ Mobile app
❌ Advanced analytics
❌ Rate limiting
❌ Authentication/login
```

**These are Phase 2** - build after MVP works

---

## 🔄 After MVP is Done

**Phase 2 Tasks:**
1. Add ML risk scoring
2. Build merchant dashboard
3. Improve UI/UX
4. Add advanced features
5. Performance optimization
6. Security hardening

---

## 🆘 Quick Help

### **Backend Issues**
```bash
# Check logs
docker-compose logs backend

# Restart
docker-compose restart backend

# Check SQLite
ls -la disputes.db

# Test API
curl http://localhost:8000/api/health
```

### **Frontend Issues**
```bash
# Check logs
docker-compose logs frontend

# Restart
docker-compose restart frontend

# Test loading
curl http://localhost:5173
```

### **CORS Issues**
- Ensure `CORS_ALLOWED_ORIGINS` includes `http://localhost:5173` in `.env.dev`

### **Port Conflicts**
```bash
# Kill process on port
lsof -i :8000
lsof -i :5173
kill -9 <PID>
```

---

## 📞 Daily Standup Template

**Each day, answer:**
1. ✅ What did I complete?
2. ⏳ What's next?
3. 🚧 What's blocking me?
4. 🐛 Any issues discovered?

---

## 🎉 MVP Complete When...

```
✅ Both developers finished their tasks
✅ Frontend and backend integrated
✅ All 4 success criteria met:
   - Form → Backend
   - Backend → Database
   - Database → Frontend
   - Status display works
✅ Deployed as docker-compose
✅ Zero errors in logs
```

**Estimated Total Time: 12-36 hours** depending on parallel/sequential work

---

**Latest Update:** February 27, 2026
**Status:** Ready to start MVP development
**Next Step:** Person A starts backend, Person B1 starts frontend
