# UPI Dispute Resolution Agent - MVP Edition

> Automated system to resolve ₹10B+ in stuck UPI transactions within 24 hours
>
> **Current Focus:** MVP (Backend + Frontend + Mock Bank API + SQLite)
>
> **Phase 2 Later:** ML Risk Scoring, Merchant Dashboard, Advanced Features

![Status](https://img.shields.io/badge/status-MVP--ready-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![React](https://img.shields.io/badge/React-18-blue)
![Docker](https://img.shields.io/badge/Docker-compose-2496ED)

## 🎯 Quick Start (2 Minutes)

```bash
git clone <repo>
cd upi-dispute-resolution
cp .env.example .env.dev
docker-compose up -d
open http://localhost:5173
```

**That's it! MVP is running.**

---

## 📖 Documentation - Start Here

```
1. MVP_QUICK_START.md      ← READ FIRST (2-minute overview)
2. MVP_OVERVIEW.md         ← Full MVP details
3. docs/04_API_CONTRACTS.md   ← API endpoints
```

For **detailed task breakdown**:
- **Backend:** [docs/ROLES_DIVISION/PERSON_A_TASKS.md](docs/ROLES_DIVISION/PERSON_A_TASKS.md)
- **Frontend:** [docs/ROLES_DIVISION/PERSON_B1_TASKS.md](docs/ROLES_DIVISION/PERSON_B1_TASKS.md)

---

## 👥 MVP Team (2 People)

| Role | Time | What to Build |
|------|------|---------------|
| **Person A** | 12h | Backend API + Mock Bank API + SQLite |
| **Person B1** | 12h | Frontend React form + status page |

---

## ✨ MVP Features

```
✅ File dispute via form
✅ Auto-verify with mock bank API
✅ Store in SQLite database
✅ Show refund status instantly
✅ NEFT reference generation
✅ Responsive UI design
✅ Docker containerization
```

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend | Java 17 + Spring Boot 3.1 |
| Frontend | React 18 + Vite + Tailwind CSS |
| Database | SQLite (persistent) |
| DevOps | Docker + Docker Compose |

---

## 📡 API (3 Endpoints)

```bash
# File dispute
POST /api/disputes
{
  "transactionId": "TXN20260227123456",
  "merchantUpi": "amazon@upi",
  "amount": 5000,
  "customerPhone": "+919876543210"
}

# Get status
GET /api/disputes/{disputeId}

# Health check
GET /api/health
```

---

## 🚀 Running MVP

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Stop services
docker-compose down

# Test API
curl http://localhost:8000/api/health
```

---

## 📁 Project Structure

```
upi-dispute-resolution/
├── backend/               # Java Spring Boot (Person A - 12h)
├── frontend/              # React Vite (Person B1 - 12h)
├── docs/                  # Complete documentation
├── docker-compose.yml     # MVP services definition
├── MVP_QUICK_START.md     # 2-minute quick start
├── MVP_OVERVIEW.md        # Full MVP overview
└── README.md              # This file
```

---

## ✅ Success Criteria

All should be TRUE when done:

- ✅ Backend running on port 8000
- ✅ Frontend running on port 5173  
- ✅ Form submits to backend
- ✅ Data saves to SQLite
- ✅ Status endpoint works
- ✅ No CORS errors
- ✅ No console errors
- ✅ Docker running stable

---

## 🐛 Quick Troubleshooting

```bash
# Backend issues
docker-compose logs backend

# Frontend issues
docker-compose logs frontend

# Port conflicts
lsof -i :8000     # Check backend
lsof -i :5173     # Check frontend

# Start fresh
docker-compose down -v
docker-compose up -d
```

---

## 📊 Timeline

**Option 1: Sequential (24 hours)**
```
Day 1 (12h):  Person A → Backend
Day 2 (12h):  Person B1 → Frontend
```

**Option 2: Parallel (12 hours)**
```
Both work simultaneously → MVP done in 12 hours
```

---

## 🎉 After MVP is Complete

**Phase 2 Features** (when core is stable):
- ML risk scoring
- Merchant dashboard
- Advanced analytics
- Performance optimization

---

## 🔗 Key Links

- **Quick Start:** [MVP_QUICK_START.md](MVP_QUICK_START.md)
- **Full Overview:** [MVP_OVERVIEW.md](MVP_OVERVIEW.md)
- **Architecture:** [docs/02_SYSTEM_ARCHITECTURE.md](docs/02_SYSTEM_ARCHITECTURE.md)
- **API Details:** [docs/04_API_CONTRACTS.md](docs/04_API_CONTRACTS.md)
- **Backend Tasks:** [docs/ROLES_DIVISION/PERSON_A_TASKS.md](docs/ROLES_DIVISION/PERSON_A_TASKS.md)
- **Frontend Tasks:** [docs/ROLES_DIVISION/PERSON_B1_TASKS.md](docs/ROLES_DIVISION/PERSON_B1_TASKS.md)

---

**Status:** MVP ready to build
**Stack:** Java 17 + React 18 + SQLite + Docker
**Time:** 12-24 hours total