# 🚀 MVP Quick Start

## In 2 Minutes

```bash
# 1. Clone & setup
git clone <repo>
cd upi-dispute-resolution
cp .env.example .env.dev

# 2. Start MVP (backend + frontend only)
docker-compose up -d

# 3. Test
curl http://localhost:8000/api/health
# Should see: { "status": "UP" }

# 4. Open browser
open http://localhost:5173
```

**Done!** You have MVP running.

---

## What is MVP?

```
✅ REST API to file disputes
✅ Mock bank verification
✅ SQLite database storage
✅ React frontend form
✅ Status tracking page

❌ NO ML risk scoring (Phase 2)
❌ NO merchant dashboard (Phase 2)
❌ NO advanced features (Phase 2)
```

---

## 👥 Who Does What?

### **Person A - Backend (12 hours)**

Create in `backend/`:
```java
Dispute.java          // Entity for database
DisputeRepository.java    // Database access
DisputeService.java      // Business logic + mock bank API
DisputeController.java    // REST endpoints
DisputeRequest.java      // Form input
DisputeResponse.java     // Response format
```

3 REST endpoints:
- `POST /api/disputes` - File new dispute
- `GET /api/disputes/{id}` - Get status
- `GET /api/health` - Check health

### **Person B1 - Frontend (12 hours)**

Create in `frontend/src/`:
```jsx
DisputeForm.jsx      // Form component
StatusPage.jsx       // Status display
App.jsx              // Main app
```

**Features:**
- Form with validation
- API integration (axios)
- Status page display
- Responsive design

---

## 📋 API Endpoints

### File Dispute
```bash
POST /api/disputes
{
  "transactionId": "TXN20260227123456",
  "merchantUpi": "amazon@upi",
  "amount": 5000,
  "customerPhone": "+919876543210"
}

Returns:
{
  "disputeId": "DIS_1709028600",
  "status": "REFUND_INITIATED",
  "neftReference": "NEFT000123456789"
}
```

### Get Status
```bash
GET /api/disputes/DIS_1709028600

Returns:
{
  "disputeId": "DIS_1709028600",
  "status": "REFUND_INITIATED",
  "neftReference": "NEFT000123456789",
  "expectedSettlement": "2025-02-28T14:30:00Z"
}
```

---

## 🗄️ Database (SQLite)

```sql
disputes
├── id (String, Primary Key)
├── transaction_id (String)
├── merchant_upi (String)
├── amount (Decimal)
├── customer_phone (String)
├── status (String)
├── neft_reference (String)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

Automatically created on first run.

---

## 🐛 Common Issues

### Backend won't start
```bash
# Check logs
docker-compose logs backend

# Rebuild
docker-compose build --no-cache backend

# Check port
lsof -i :8000
```

### Frontend won't load
```bash
# Check logs
docker-compose logs frontend

# Rebuild
docker-compose build --no-cache frontend

# Check port
lsof -i :5173
```

### CORS errors
- Edit `.env.dev`
- Ensure `CORS_ALLOWED_ORIGINS=http://localhost:5173`

---

## ✅ MVP Done When

- ✅ Backend running on 8000
- ✅ Frontend running on 5173
- ✅ Form submits successfully
- ✅ Data saves to SQLite
- ✅ Status shows correctly
- ✅ No errors in logs
- ✅ All in docker-compose

---

## 📚 Documentation

**For Details:**
- Backend tasks: [docs/ROLES_DIVISION/PERSON_A_TASKS.md](docs/ROLES_DIVISION/PERSON_A_TASKS.md)
- Frontend tasks: [docs/ROLES_DIVISION/PERSON_B1_TASKS.md](docs/ROLES_DIVISION/PERSON_B1_TASKS.md)
- Full overview: [MVP_OVERVIEW.md](MVP_OVERVIEW.md)
- Architecture: [docs/02_SYSTEM_ARCHITECTURE.md](docs/02_SYSTEM_ARCHITECTURE.md)

---

## 🎯 Success Criteria

```javascript
const mvpComplete = {
  backend_running: true,
  frontend_running: true,
  form_submits: true,
  data_persists: true,
  status_displays: true,
  no_errors: true,
  dockerized: true
};

if (Object.values(mvpComplete).every(v => v === true)) {
  console.log("🎉 MVP COMPLETE!");
}
```

---

## 🔄 After MVP

When MVP is **working and stable**:

1. Add Phase 2: ML risk scoring
2. Add Phase 2: Merchant dashboard
3. Add Phase 2: Advanced features

For now: **Focus on getting MVP working first!**

---

**Ready? Start with [MVP_OVERVIEW.md](MVP_OVERVIEW.md) for detailed breakdown!**
