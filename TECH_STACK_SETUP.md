# Tech Stack & Implementation Setup Guide

## 🛠️ FINAL TECH STACK (12-Hour Hackathon)

```
FRONTEND:
├─ React 18 (UI framework)
├─ Vite (fast build, hot reload)
├─ Axios (API calls)
├─ Recharts (simple charts for dashboard)
└─ Tailwind CSS (styling, pre-built components)

BACKEND:
├─ Python 3.11
├─ FastAPI (fast, async HTTP framework)
├─ SQLAlchemy (database ORM)
├─ Pydantic (data validation)
└─ SQLite (lightweight database, no setup)

DATABASE:
├─ SQLite (for hackathon, zero setup)
└─ PostgreSQL (if you want to upgrade later)

TOOLS:
├─ Git (version control)
├─ Postman (test APIs without frontend)
├─ Mock APIs (simulate banks, built-in)
└─ GitHub (deployment ready)
```

---

## 📦 Installation & Setup (Step by Step)

### **1. SETUP PROJECT FOLDER**

```bash
# Create project directory
mkdir upi-dispute-system
cd upi-dispute-system

# Initialize Git
git init
git config user.name "Your Name"
git config user.email "your@email.com"

# Create folder structure
mkdir backend frontend mock_apis docs
touch README.md .gitignore
```

### **2. BACKEND SETUP (Python + FastAPI)**

```bash
# Go to backend folder
cd backend

# Create Python virtual environment
python3.11 -m venv venv

# Activate virtual environment
# On Mac/Linux:
source venv/bin/activate

# On Windows:
venv\Scripts\activate

# Create requirements.txt
cat > requirements.txt << EOF
fastapi==0.104.1
uvicorn==0.24.0
sqlalchemy==2.0.23
pydantic==2.5.0
python-multipart==0.0.6
pytest==7.4.3
python-dotenv==1.0.0
EOF

# Install dependencies
pip install -r requirements.txt

# Verify FastAPI installed
python -c "import fastapi; print(fastapi.__version__)"
```

### **3. FRONTEND SETUP (React + Vite)**

```bash
# Go to frontend folder (from project root)
cd frontend

# Create React app with Vite
npm create vite@latest . -- --template react

# Install dependencies
npm install

# Install additional libraries
npm install axios recharts tailwindcss

# Start dev server (for testing later)
npm run dev  # Runs on http://localhost:5173
```

### **4. DATABASE SETUP (SQLite)**

```bash
# SQLite comes with Python, no separate installation needed
# Create database folder in backend
mkdir -p backend/database

# Create SQLite database (auto-created when you first run app)
# Just connect in your code: sqlite:///./database/disputes.db
```

---

## 📁 FOLDER STRUCTURE (Copy This)

```
upi-dispute-system/
├─ README.md
├─ .gitignore
├─ requirements.txt
│
├─ backend/
│  ├─ venv/                      (created by Python)
│  ├─ requirements.txt
│  ├─ main.py                    (FastAPI app starts here)
│  ├─ .env                       (config, secrets)
│  │
│  ├─ database/
│  │  ├─ db.py                   (SQLAlchemy setup)
│  │  └─ disputes.db             (auto-created SQLite)
│  │
│  ├─ models/
│  │  ├─ dispute.py              (Dispute model)
│  │  ├─ transaction.py          (Transaction model)
│  │  └─ merchant.py             (Merchant model)
│  │
│  ├─ routes/
│  │  ├─ disputes.py             (POST /disputes, GET /disputes/{id})
│  │  ├─ verification.py         (Bank verification logic)
│  │  ├─ risk.py                 (Risk score calculation)
│  │  └─ merchants.py            (Dashboard data)
│  │
│  ├─ services/
│  │  ├─ bank_api.py             (Mock bank API calls)
│  │  ├─ risk_scorer.py          (Risk calculation logic)
│  │  └─ neft_processor.py        (NEFT refund logic)
│  │
│  └─ tests/
│     └─ test_disputes.py        (Unit tests)
│
├─ frontend/
│  ├─ node_modules/              (created by npm)
│  ├─ package.json
│  ├─ vite.config.js
│  ├─ index.html
│  │
│  ├─ src/
│  │  ├─ App.jsx                 (Main component)
│  │  ├─ main.jsx                (Entry point)
│  │  ├─ index.css               (Tailwind styles)
│  │  │
│  │  ├─ components/
│  │  │  ├─ DisputeForm.jsx      (File dispute form)
│  │  │  ├─ RiskScore.jsx        (Show risk warning)
│  │  │  ├─ StatusTracker.jsx    (Track refund progress)
│  │  │  └─ Dashboard.jsx        (Merchant health)
│  │  │
│  │  ├─ pages/
│  │  │  ├─ Home.jsx
│  │  │  ├─ Dashboard.jsx
│  │  │  └─ Status.jsx
│  │  │
│  │  └─ services/
│  │     └─ api.js               (Axios instance for backend calls)
│
├─ mock_apis/
│  ├─ bank_responses.json        (Sample bank API responses)
│  └─ test_data.json             (Sample disputes for testing)
│
└─ docs/
   ├─ API.md                     (API documentation)
   ├─ SETUP.md                   (This setup guide)
   └─ FORMATS.md                 (Input/output formats)
```

---

## 🚀 CRITICAL FILES TO CREATE FIRST

### **1. Backend Entry Point: `backend/main.py`**

```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from database.db import init_db
import routes.disputes as disputes
import routes.risk as risk
import routes.merchants as merchants

app = FastAPI(
    title="UPI Dispute Resolution",
    description="Resolve failed UPI transactions in 24 hours",
    version="1.0.0"
)

# Enable CORS (so frontend can call backend)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Frontend URL: http://localhost:5173
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Initialize database
init_db()

# Include routes
app.include_router(disputes.router, prefix="/api")
app.include_router(risk.router, prefix="/api")
app.include_router(merchants.router, prefix="/api")

@app.get("/")
async def root():
    return {"message": "UPI Dispute Resolution API is running"}

@app.get("/health")
async def health():
    return {"status": "healthy"}

# Run with: uvicorn main:app --reload
```

### **2. Database Connection: `backend/database/db.py`**

```python
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
import os

# SQLite database
DATABASE_URL = "sqlite:///./database/disputes.db"

engine = create_engine(
    DATABASE_URL,
    connect_args={"check_same_thread": False}
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def init_db():
    Base.metadata.create_all(bind=engine)
    print("✅ Database initialized")
```

### **3. Dispute Model: `backend/models/dispute.py`**

```python
from sqlalchemy import Column, String, Float, DateTime, Boolean, Integer
from database.db import Base
from datetime import datetime

class Dispute(Base):
    __tablename__ = "disputes"
    
    dispute_id = Column(String, primary_key=True, index=True)
    transaction_id = Column(String, unique=True, index=True)
    merchant_upi = Column(String, index=True)
    customer_phone = Column(String)
    amount = Column(Float)
    
    status = Column(String)  # CREATED, VERIFYING, APPROVED, REFUNDING, SETTLED
    verification_status = Column(String)  # VERIFIED_FAILED, UNCLEAR
    approval_decision = Column(String)  # APPROVED, REJECTED
    
    customer_debited = Column(Boolean, nullable=True)
    merchant_credited = Column(Boolean, nullable=True)
    verification_confidence = Column(Integer, default=0)  # 0-100
    
    neft_reference = Column(String, nullable=True)
    settlement_date = Column(DateTime, nullable=True)
    
    created_at = Column(DateTime, default=datetime.utcnow)
    verified_at = Column(DateTime, nullable=True)
    approved_at = Column(DateTime, nullable=True)
    refund_initiated_at = Column(DateTime, nullable=True)
    settled_at = Column(DateTime, nullable=True)
```

### **4. Dispute Routes: `backend/routes/disputes.py`**

```python
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models.dispute import Dispute
from database.db import get_db
from datetime import datetime
import uuid

router = APIRouter()

@router.post("/disputes")
async def file_dispute(
    transaction_id: str,
    merchant_upi: str,
    amount: float,
    customer_phone: str,
    db: Session = Depends(get_db)
):
    """File a new dispute"""
    
    # Check if dispute already exists
    existing = db.query(Dispute).filter(
        Dispute.transaction_id == transaction_id
    ).first()
    
    if existing:
        raise HTTPException(status_code=409, detail="Dispute already filed")
    
    # Create new dispute
    dispute_id = f"DIS_{int(datetime.utcnow().timestamp())}"
    
    dispute = Dispute(
        dispute_id=dispute_id,
        transaction_id=transaction_id,
        merchant_upi=merchant_upi,
        amount=amount,
        customer_phone=customer_phone,
        status="CREATED"
    )
    
    db.add(dispute)
    db.commit()
    db.refresh(dispute)
    
    return {
        "success": True,
        "dispute_id": dispute_id,
        "status": "CREATED",
        "message": "Dispute filed. Verifying with banks...",
        "reference_number": dispute_id
    }

@router.get("/disputes/{dispute_id}")
async def get_dispute(dispute_id: str, db: Session = Depends(get_db)):
    """Get dispute status"""
    
    dispute = db.query(Dispute).filter(
        Dispute.dispute_id == dispute_id
    ).first()
    
    if not dispute:
        raise HTTPException(status_code=404, detail="Dispute not found")
    
    return {
        "dispute_id": dispute.dispute_id,
        "transaction_id": dispute.transaction_id,
        "amount": dispute.amount,
        "status": dispute.status,
        "created_at": dispute.created_at,
        "verified_at": dispute.verified_at,
        "neft_reference": dispute.neft_reference
    }
```

### **5. Risk Score Service: `backend/services/risk_scorer.py`**

```python
def calculate_risk_score(transaction_data):
    """Calculate risk score (0-1) for a transaction"""
    
    risk = 0
    
    # Factor 1: Merchant failure rate (25%)
    merchant_failure_rate = transaction_data.get("merchant_failure_rate", 0.01)
    risk += merchant_failure_rate * 0.25
    
    # Factor 2: Time of day (15%)
    hour = transaction_data.get("time_of_day", 12)
    if 2 <= hour <= 4:  # 2-4 AM = risky
        risk += 0.15
    
    # Factor 3: Amount (10%)
    amount = transaction_data.get("amount", 0)
    if amount > 50000:
        risk += 0.10
    
    # Factor 4: Network type (15%)
    network = transaction_data.get("network_type", "4G")
    if network == "4G":
        risk += 0.15
    elif network == "WiFi":
        risk += 0.05
    
    # Factor 5: Device age (10%)
    device_age = transaction_data.get("device_age_months", 24)
    if device_age > 36:
        risk += 0.10
    elif device_age < 12:
        risk += 0.02
    
    # Factor 6: Customer success rate (5%)
    customer_success_rate = transaction_data.get("customer_success_rate", 0.90)
    risk += (1 - customer_success_rate) * 0.05
    
    # Cap at 1.0
    total_risk = min(risk, 1.0)
    
    return {
        "risk_score": round(total_risk, 2),
        "risk_percentage": f"{round(total_risk * 100)}%",
        "risk_level": "HIGH" if total_risk > 0.6 else "MEDIUM" if total_risk > 0.3 else "LOW",
        "should_flag": total_risk > 0.6
    }
```

### **6. Frontend: `frontend/src/components/DisputeForm.jsx`**

```jsx
import { useState } from 'react';
import axios from 'axios';

export default function DisputeForm() {
  const [formData, setFormData] = useState({
    transaction_id: '',
    merchant_upi: '',
    amount: '',
    customer_phone: ''
  });
  
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const res = await axios.post(
        'http://localhost:8000/api/disputes',
        formData
      );
      
      setResponse(res.data);
      setFormData({
        transaction_id: '',
        merchant_upi: '',
        amount: '',
        customer_phone: ''
      });
    } catch (error) {
      setResponse({
        success: false,
        message: error.response?.data?.detail || 'Error filing dispute'
      });
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-lg shadow">
      <h1 className="text-2xl font-bold mb-6">File UPI Dispute</h1>
      
      <form onSubmit={handleSubmit} className="space-y-4">
        <input
          type="text"
          placeholder="Transaction ID"
          value={formData.transaction_id}
          onChange={(e) => setFormData({...formData, transaction_id: e.target.value})}
          className="w-full p-2 border border-gray-300 rounded"
          required
        />
        
        <input
          type="text"
          placeholder="Merchant UPI"
          value={formData.merchant_upi}
          onChange={(e) => setFormData({...formData, merchant_upi: e.target.value})}
          className="w-full p-2 border border-gray-300 rounded"
          required
        />
        
        <input
          type="number"
          placeholder="Amount"
          value={formData.amount}
          onChange={(e) => setFormData({...formData, amount: e.target.value})}
          className="w-full p-2 border border-gray-300 rounded"
          required
        />
        
        <input
          type="tel"
          placeholder="Phone Number"
          value={formData.customer_phone}
          onChange={(e) => setFormData({...formData, customer_phone: e.target.value})}
          className="w-full p-2 border border-gray-300 rounded"
          required
        />
        
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600 disabled:opacity-50"
        >
          {loading ? 'Filing...' : 'File Dispute'}
        </button>
      </form>
      
      {response && (
        <div className={`mt-4 p-4 rounded ${response.success ? 'bg-green-100' : 'bg-red-100'}`}>
          <h3 className="font-bold">
            {response.success ? '✅ Success!' : '❌ Error'}
          </h3>
          <p>{response.message}</p>
          {response.dispute_id && (
            <p className="mt-2 font-mono text-sm">
              Dispute ID: {response.dispute_id}
            </p>
          )}
        </div>
      )}
    </div>
  );
}
```

---

## ▶️ HOW TO RUN (Step by Step)

### **Terminal 1: Start Backend**

```bash
cd backend

# Activate virtual environment
source venv/bin/activate  # Mac/Linux
# or
venv\Scripts\activate  # Windows

# Run FastAPI server
uvicorn main:app --reload

# You'll see:
# ✨ Uvicorn running on http://127.0.0.1:8000
# 📚 Docs at http://127.0.0.1:8000/docs
```

### **Terminal 2: Start Frontend**

```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start React dev server
npm run dev

# You'll see:
# ➜  Local:   http://localhost:5173/
```

### **Test the System**

1. **Option A: Use Browser**
   - Go to http://localhost:5173
   - Fill the form, submit
   - Should get dispute ID back ✅

2. **Option B: Use FastAPI Docs (Swagger UI)**
   - Go to http://localhost:8000/docs
   - Click "Try it out" on /api/disputes
   - Fill in data, execute
   - See JSON response

3. **Option C: Use cURL (Terminal)**
   ```bash
   curl -X POST "http://localhost:8000/api/disputes" \
     -H "Content-Type: application/json" \
     -d '{
       "transaction_id": "TEST001",
       "merchant_upi": "amazon@upi",
       "amount": 5000,
       "customer_phone": "+919876543210"
     }'
   ```

---

## 📚 DEPENDENCIES CHECKLIST

### **Python Dependencies** (`backend/requirements.txt`)
```
fastapi==0.104.1          ← Web framework
uvicorn==0.24.0           ← Server
sqlalchemy==2.0.23        ← Database ORM
pydantic==2.5.0           ← Data validation
python-multipart==0.0.6   ← Form data handling
pytest==7.4.3             ← Testing
python-dotenv==1.0.0      ← Environment variables
```

### **Node Dependencies** (`frontend/package.json`)
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "axios": "^1.6.0",
    "recharts": "^2.10.0",
    "tailwindcss": "^3.3.0"
  },
  "devDependencies": {
    "vite": "^5.0.0",
    "@vitejs/plugin-react": "^4.2.0"
  }
}
```

---

## 🔑 CRITICAL ENVIRONMENT FILE: `backend/.env`

```env
# Database
DB_URL=sqlite:///./database/disputes.db

# API Settings
API_PORT=8000
API_HOST=0.0.0.0

# Mock Bank APIs
MOCK_BANK_DELAY_MS=1000
BANK_VERIFICATION_TIMEOUT=5

# Feature Flags
ENABLE_RISK_SCORING=true
ENABLE_AUTO_REFUND=true
ENABLE_MERCHANT_DASHBOARD=true

# Logging
LOG_LEVEL=INFO
```

---

## ✅ INSTALLATION COMMANDS (Copy Paste)

### **For Mac/Linux:**
```bash
# Setup backend
cd backend
python3.11 -m venv venv
source venv/bin/activate
pip install -r requirements.txt

# Setup frontend
cd ../frontend
npm install
npm install axios recharts

# Done!
```

### **For Windows:**
```bash
# Setup backend
cd backend
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt

# Setup frontend
cd ..\frontend
npm install
npm install axios recharts

# Done!
```

---

## 🎯 START CODING CHECKLIST

- [ ] Create project folder and git repo
- [ ] Setup backend (Python venv, FastAPI, SQLAlchemy)
- [ ] Setup frontend (React, Vite, dependencies)
- [ ] Create folder structure (models, routes, components)
- [ ] Create `backend/main.py` (entry point)
- [ ] Create `backend/database/db.py` (SQLAlchemy setup)
- [ ] Create `backend/models/dispute.py` (Data model)
- [ ] Create `backend/routes/disputes.py` (API endpoints)
- [ ] Create `frontend/src/components/DisputeForm.jsx` (Form UI)
- [ ] Test: Start backend, start frontend, try filing dispute
- [ ] Add risk scoring logic
- [ ] Add merchant dashboard
- [ ] Add status tracker
- [ ] Polish UI and demo

**Estimated time to get first endpoint working: 30 minutes** ⏱️

---

## 🚨 COMMON ISSUES & FIXES

**Issue: "Module not found" (Python)**
```bash
# Solution: Make sure venv is activated
source venv/bin/activate  # Mac/Linux
venv\Scripts\activate     # Windows
```

**Issue: "Port 8000 already in use"**
```bash
# Solution: Use different port
uvicorn main:app --reload --port 8001
```

**Issue: "CORS error" (Frontend can't call backend)**
```python
# Solution: Already in main.py, but add this if needed:
from fastapi.middleware.cors import CORSMiddleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

**Issue: "Database error"**
```python
# Solution: Make sure database folder exists
mkdir -p backend/database
# SQLite will auto-create disputes.db on first run
```

---

## 📖 Quick Reference Links

- **FastAPI Docs:** https://fastapi.tiangolo.com/
- **React Docs:** https://react.dev/
- **SQLAlchemy Docs:** https://docs.sqlalchemy.org/
- **Tailwind CSS:** https://tailwindcss.com/
- **Axios Docs:** https://axios-http.com/

---

**You're ready to code! Start with `backend/main.py` and build from there.** 🚀
