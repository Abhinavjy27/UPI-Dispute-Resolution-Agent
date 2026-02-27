# UPI Dispute Resolution Agent - START HERE 🚀

**Hackathon Project**: AI-Powered UPI Dispute Resolution Platform  
**Status**: Ready for Development (24-hour sprint)  
**Date**: February 27, 2026

---

## 📋 Quick Overview

### What You're Building
An automated system that resolves failed UPI transaction disputes in **2-4 hours** instead of 5-7 days, using AI fraud detection.

### Problem → Solution
- **Before**: Customer disputes UPI tx → Bank takes 5-7 days → Manual verification
- **After**: Dispute submitted → AI fraud check (10 min) → Auto-approve if LOW risk → Refund in 2-4 hours

### Core Features
1. **Dispute Submission** - Customer submits failed UPI tx
2. **Bank Verification** - System verifies with bank API
3. **Fraud Detection** - ML model analyzes risk score
4. **Auto-Refund Decision** - Approves/rejects based on risk
5. **Real-Time Tracking** - Customer sees live status updates
6. **Admin Dashboard** - Analytics with risk charts

---

## 🏗️ Tech Stack (Fintech-Optimized)

```
Frontend:     React 18 + Vite + Tailwind CSS
Backend:      Java 17 + Spring Boot 3 + Lombok + MapStruct
ML Service:   Python 3.11 + FastAPI + scikit-learn
Queue:        RabbitMQ (event-driven)
Database:     PostgreSQL (ACID compliance)
Cache:        Redis (idempotency, rate limiting)
DevOps:       Docker Compose
```

**Why Java?**  
Money operations need BigDecimal (no float precision loss), @Transactional (ACID), type safety.

---

## 📁 Project Structure (4 Core Files)

```
UPI-Dispute-Resolution-Agent/
├── README_START_HERE.md (YOU ARE HERE)
├── SETUP_AND_ARCHITECTURE.md (Tech decisions + System design)
├── API_AND_DATABASE.md (All endpoints + DB schema + enums)
├── TASK_BREAKDOWN.md (24-hour sprint tasks)
│
├── src/
│   ├── frontend/ (React Vite app)
│   ├── backend/ (Spring Boot API)
│   └── ml/ (FastAPI fraud service)
│
└── docker-compose.yml
```

---

## 🚀 Quick Start (First 30 Minutes)

### Step 1: Read These 4 Files (In Order)
1. **README_START_HERE.md** (this file) ← Overview
2. **SETUP_AND_ARCHITECTURE.md** ← Tech decisions & design
3. **API_AND_DATABASE.md** ← Contracts & schema
4. **TASK_BREAKDOWN.md** ← What to build

### Step 2: Divide Work (2-Person Team)
- **Person A**: Frontend React + ML fraud service
- **Person B**: Backend Spring Boot + Database + RabbitMQ

### Step 3: Day-Of Setup (30 min)
- Person A: Clone repo, `npm install`, `npm run dev`
- Person B: Create Spring Boot project, `mvn spring-boot:run`
- Both: `docker-compose up` (PostgreSQL + Redis + RabbitMQ)

### Step 4: Start Building
- Phase 1 (0-2h): Setup infrastructure
- Phase 2 (2-8h): Core development (REST API, components)
- Phase 3 (8-12h): Integration testing
- Phase 4 (12-20h): Polish & bug fixes
- Phase 5 (20-24h): Deploy + demo

---

## 📊 Workflow (High-Level)

```
User Submits Dispute
    ↓
Frontend Form Validation
    ↓
Spring Boot API: Create Dispute (SUBMITTED)
    ↓
RabbitMQ Event: disputes.created
    ↓
Bank Mock API: Verify Transaction
    ↓
Update Status: VERIFIED → RabbitMQ Event
    ↓
FastAPI ML Service: Score Fraud Risk (0.0-1.0)
    ↓
Spring Boot Decision Engine:
    • LOW (0-0.30) → Auto-approve
    • MEDIUM (0.31-0.70) → Manual review
    • HIGH (0.71-1.0) → Auto-reject
    ↓
Update Status: APPROVED/REJECTED
    ↓
Initiate Refund (NEFT)
    ↓
Frontend: Real-time status updates (polling every 30s)
    ↓
Dashboard: Metrics updated live
```

---

## 📈 Success Metrics (What You Need)

By Hour 24, verify these work:

✅ Dispute submission form works  
✅ Disputes appear in real-time list  
✅ Status timeline shows all transitions  
✅ Fraud ML service scores disputes  
✅ Low-risk disputes auto-approve  
✅ Admin dashboard shows 4+ charts  
✅ All 4 API endpoints responding (201, 200, 200, 400/404/409)  
✅ PostgreSQL has all 6 tables  
✅ RabbitMQ events publishing correctly  
✅ Responsive design (mobile + desktop)  
✅ Deployed on Railway (live URL works)  
✅ Demo script rehearsed (5 min walkthrough)

---

## 🎯 What NOT To Do

❌ Don't build separate repo for frontend, backend, ml  
❌ Don't spend time on beautiful UI (functional is enough)  
❌ Don't implement EVERY edge case error handling  
❌ Don't refactor code (finalize = move to next feature)  
❌ Don't test with real UPI (use mocked bank API)  
❌ Don't build admin approval UI (manual review can be console log)  
❌ Don't optimize database queries (premature optimization)  
❌ Don't build user authentication (use mock JWT)

---

## 📖 Read Next

### For Tech Decisions & Why Choices
→ Read **SETUP_AND_ARCHITECTURE.md**

### For API Contracts, Enums, DB Schema
→ Read **API_AND_DATABASE.md**

### For Hour-by-Hour Task Breakdown
→ Read **TASK_BREAKDOWN.md**

---

## 🎓 Key Concepts You Need

### 1. Event-Driven Architecture
- Service A does something → publishes event to RabbitMQ
- Service B listens → reacts to event
- Decouples services, enables async processing

### 2. BigDecimal (Not Float!)
```java
// WRONG: ₹5000.10 + ₹0.10 = ₹5000.200000000001 ❌
float amount = 5000.10f;

// RIGHT: ₹5000.10 + ₹0.10 = ₹5000.20 ✅
BigDecimal amount = new BigDecimal("5000.10");
```

### 3. Idempotency Keys
- Same request sent twice = same result (no duplicate disputes)
- Generate UUID for each request
- Check Redis before creating

### 4. Risk Scoring (0.0 to 1.0)
- 0.00-0.30: LOW (Auto-approve) ✅
- 0.31-0.70: MEDIUM (Manual review) ⏳
- 0.71-1.00: HIGH (Auto-reject) ❌

### 5. ML Model in FastAPI
- Takes: upi_tx_id, amount, customer_history
- Returns: fraud_score, risk_level, explanations (SHAP)
- For MVP: Use Isolation Forest (no labeled data needed)

---

## 🔧 Dependencies Checklist

### All Members
- [ ] Git (for version control)
- [ ] Docker Desktop (for containers)
- [ ] IDE: IntelliJ (Java) + VS Code (React/Python)

### Person A (Frontend + ML)
- [ ] Node.js 18+
- [ ] Python 3.11+
- [ ] npm (includes with Node)

### Person B (Backend + Database)
- [ ] Java 17 JDK
- [ ] Maven 3.8+
- [ ] PostgreSQL client (psql)

---

## 💡 Pro Tips

1. **Use Lombok** - Cut Java boilerplate 50% (@Data, @Builder)
2. **Use MapStruct** - Auto-generate DTO mappers
3. **Use Spring Data REST** - Auto-generates CRUD APIs
4. **Use Testcontainers** - Auto-spin up DB/RabbitMQ for testing
5. **Git Commit Frequently** - Every feature, don't wait til end
6. **Demo Every 4 Hours** - Make sure person A & B code works together
7. **Pre-load Test Data** - Don't spend time filling forms during demo

---

## ❓ FAQ

**Q: What if a feature doesn't work by Hour 24?**  
A: Skip it, comment out the code, move to demo. A working 80% demo beats a broken 100%.

**Q: Can we use different tech?**  
A: NO. Stick with stack. Switching mid-hackathon = disaster.

**Q: What about unit tests?**  
A: Add them if time permits. For 24h, focus on E2E working.

**Q: How do we sync code?**  
A: Git + hourly syncs. Merge PRs every 2 hours.

**Q: What if database is slow?**  
A: It won't be (PostgreSQL is fast). If it is, add Redis cache.

**Q: What if ML model is inaccurate?**  
A: It will be (MVP using Isolation Forest). That's fine - judges expect mock.

---

## 🎬 Presentation Tips (Hour 23-24)

**Demo Script (5 minutes):**
1. Show problem: "Customers wait 5-7 days"
2. Show solution: Open app, create dispute
3. Show tracking: Status updates in real-time
4. Show admin dashboard: Charts with metrics
5. Key message: "2-4 hours instead of 5-7 days, 95% automated"

**Slides (3-5 slides):**
1. Problem & Solution
2. Architecture Diagram (with boxes, arrows)
3. Features & Tech Stack
4. Live Demo Screenshot
5. Impact (Resolution time down 95%)

---

## 📞 Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| Spring Boot won't start | Check PostgreSQL is running: `docker-compose up` |
| React app blank | Check Vite dev server: `npm run dev` |
| ML service errors | Check Python installed: `python --version` |
| RabbitMQ queue empty | Check queue listener is running |
| Database migrations fail | Drop DB and restart: `docker-compose down -v && docker-compose up` |
| API returns 401 | Check JWT token in header |
| Fraud score always 0.0 | Check FastAPI service is responding |

---

## 🚢 Deployment (Hour 20-24)

### Frontend (Vercel)
```bash
npm run build
git push origin main
# Vercel auto-deploys
```

### Backend (Railway)
```bash
git push origin main
# Railway auto-builds + deploys Docker
```

### Live URL
Frontend: `https://yourname-frontend.vercel.app`  
Backend API: `https://yourname-backend.railway.app/api`

---

## 📎 Files Reference

| File | Purpose | Read Time |
|------|---------|-----------|
| README_START_HERE.md | Overview (you are here) | 5 min |
| SETUP_AND_ARCHITECTURE.md | Tech + Design decisions | 20 min |
| API_AND_DATABASE.md | Contracts + Schema | 20 min |
| TASK_BREAKDOWN.md | Hour-by-hour tasks | 15 min |

---

## ✨ Ready to Build?

1. Read **SETUP_AND_ARCHITECTURE.md** next
2. Understand why Java + Spring Boot is chosen
3. Review the system architecture diagram
4. Then read **API_AND_DATABASE.md** for contracts
5. Finally, read **TASK_BREAKDOWN.md** to start building

**Estimated total reading time: 60 minutes**  
**Then you're ready to code!**

---

**Let's build something amazing! 🚀**
