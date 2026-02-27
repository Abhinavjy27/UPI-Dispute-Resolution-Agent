# 02_SYSTEM_ARCHITECTURE

## 🏗️ System Overview

```
                    CUSTOMER
                      │
                      │ (Files dispute)
                      ▼
            ┌─────────────────────┐
            │  React Frontend     │
            │  (Dispute Form)     │
            └──────────┬──────────┘
                       │ POST /api/disputes
                       │ {transaction_id, merchant_upi, amount, phone}
                       │
                       ▼
            ┌─────────────────────┐
            │   FastAPI Backend   │
            │   (Verification)    │
            └──────────┬──────────┘
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            ▼            ▼
        [DB]      [CUST API]  [MERCHANT API]
       SQLite      Bank 1    Bank 2
       
       ↓            ↓            ↓
     Store        "Was ₹5000   "Did UPI
     Dispute      debited?"     reach?"
     
                    ▼
            ┌─────────────────────┐
            │  Verify + Decide    │
            │  (Auto-Approve)     │
            └──────────┬──────────┘
                       │
          If verified failure:
                       │
                       ▼
            ┌─────────────────────┐
            │  Initiate NEFT      │
            │  Refund Engine      │
            └──────────┬──────────┘
                       │
                       ▼
                  CUSTOMER REFUNDED
                  (24 hours)
```

---

## 🔄 Detailed Flow

### **Step 1: Customer Files Dispute (0 min)**

```
UI Form Fields:
├─ Transaction ID: "TXN20260227123456"
├─ Merchant UPI: "amazon@upi"
├─ Amount: ₹5,000
└─ Phone: "+919876543210"

POST /api/disputes
├─ Validate inputs
├─ Check if transaction ID format valid
├─ Create dispute record in DB (status: PENDING)
└─ Return: dispute_id = "DIS_1709028600"

Response to User:
├─ "Dispute filed successfully"
├─ "Verification in progress..."
└─ "We'll notify you by SMS"
```

### **Step 2: Call Bank APIs in Parallel (1-2 min)**

```
Backend Logic:

dispute = create_dispute_record(...)

# Call both banks at same time (parallel)
customer_bank_response = await call_customer_bank_api(
    transaction_id: "TXN20260227123456",
    account_id: "xxxxxxxx1234",
    query: "Was ₹5000 debited?"
)

merchant_bank_response = await call_merchant_bank_api(
    merchant_upi: "amazon@upi",
    query: "Was ₹5000 credited on this UPI?",
    timestamp: "2026-02-27T14:30:00Z"
)

Results Example:
├─ Customer Bank: {"debited": true, "amount": 5000}
└─ Merchant Bank: {"credited": false, "received": 0}
```

### **Step 3: Verify and Decide (< 1 sec)**

```
Decision Logic:

if customer_bank.debited AND NOT merchant_bank.credited:
    status = "VERIFIED_FAILURE"
    approval = "AUTO_APPROVED"
    risk = "LOW"  # Clear case
    confidence = 99%
    
elif customer_bank.debited AND merchant_bank.credited:
    status = "DISPUTED_INCORRECT"  # User filed false claim
    approval = "REJECTED"
    reason = "Money was received by merchant"
    
elif NOT customer_bank.debited AND merchant_bank.credited:
    status = "ANOMALY"  # Something weird happened
    approval = "MANUAL_REVIEW"  # Escalate to ops
    risk = "HIGH"
    
else:
    status = "UNKNOWN"  # Rare edge case
    approval = "MANUAL_REVIEW"

Update DB:
├─ status: "VERIFIED_FAILURE"
├─ customer_bank_response: {...}
├─ merchant_bank_response: {...}
├─ confidence_score: 99%
├─ approval_status: "AUTO_APPROVED"
└─ refund_initiated_at: NOW
```

### **Step 4: Initiate Refund (2-3 min)**

```
Refund Engine:

Create NEFT transaction:
├─ From: Merchant settlement account (or platform reserve)
├─ To: Customer account (extracted from transaction ID)
├─ Amount: ₹5,000
├─ Reference: "TXN20260227123456_REFUND_DIS_1709028600"
├─ Processing date: Same day (next settlement cycle)
└─ Expected delivery: Next day by 9 AM

API Call to Bank NEFT Service:
POST /api/neft/initiate
{
    "beneficiary_account": "xxxxxxxxxxxx",
    "amount": 5000,
    "description": "Failed UPI refund for TXN20260227123456",
    "reference": "DIS_1709028600"
}

Response:
{
    "neft_id": "NEFT20260227123456",
    "status": "PROCESSING",
    "expected_settlement": "2026-02-28T09:00:00Z"
}

Update DB:
├─ status: "REFUND_INITIATED"
├─ neft_reference: "NEFT20260227123456"
├─ refund_amount: 5000
└─ expected_settlement: "2026-02-28T09:00:00Z"
```

### **Step 5: Notify Customer (3-5 min)**

```
SMS to Customer:
"Your refund of ₹5,000 has been initiated. Reference: NEFT20260227123456. 
Money will reach your account by tomorrow 9 AM. - YourFintechApp"

Email Summary:
├─ Subject: "Refund Processed for Failed Transaction"
├─ Dispute ID: DIS_1709028600
├─ Amount: ₹5,000
├─ NEFT Reference: NEFT20260227123456
├─ Expected Settlement: Feb 28, 9:00 AM
└─ Tracking link: www.yourapp.com/disputes/DIS_1709028600

Customer Portal:
GET /api/disputes/{dispute_id}
{
    "dispute_id": "DIS_1709028600",
    "status": "REFUND_INITIATED",
    "amount": 5000,
    "neft_reference": "NEFT20260227123456",
    "expected_settlement": "2026-02-28T09:00:00Z",
    "created_at": "2026-02-27T14:35:00Z",
    "message": "Your refund is being processed. Money by tomorrow 9 AM."
}
```

---

## 📊 Database Schema

### **Disputes Table**

```
Columns:
├─ id (String, Primary Key)           DIS_1709028600
├─ transaction_id (String, Unique)    TXN20260227123456
├─ merchant_upi (String)              amazon@upi
├─ customer_phone (String)            +919876543210
├─ amount (Float)                     5000
├─ status (String)                    VERIFIED_FAILURE
│  ├─ PENDING → VERIFIED_FAILURE
│  ├─          → REFUND_INITIATED
│  ├─          → REFUND_SETTLED
│  ├─          → REJECTED (false claim)
│  ├─          → MANUAL_REVIEW (edge case)
│  └─          → CLOSED
│
├─ customer_bank_debited (Boolean)    true
├─ merchant_bank_credited (Boolean)   false
├─ confidence_score (Float)           0.99
├─ approval_status (String)           AUTO_APPROVED
│  ├─ AUTO_APPROVED (verified failure)
│  ├─ MANUAL_REVIEW (unclear case)
│  └─ REJECTED (false claim)
│
├─ neft_reference (String)            NEFT20260227123456
├─ refund_amount (Float)              5000
│
├─ created_at (DateTime)              2026-02-27 14:30:00
├─ verified_at (DateTime)             2026-02-27 14:32:00
├─ refund_initiated_at (DateTime)     2026-02-27 14:33:00
├─ expected_settlement_at (DateTime)  2026-02-28 09:00:00
├─ settled_at (DateTime)              NULL (settle next day)
└─ updated_at (DateTime)              2026-02-27 14:35:00

Example Record:
{
    "id": "DIS_1709028600",
    "transaction_id": "TXN20260227123456",
    "merchant_upi": "amazon@upi",
    "customer_phone": "+919876543210",
    "amount": 5000,
    "status": "REFUND_INITIATED",
    "customer_bank_debited": true,
    "merchant_bank_credited": false,
    "confidence_score": 0.99,
    "approval_status": "AUTO_APPROVED",
    "neft_reference": "NEFT20260227123456",
    "refund_amount": 5000,
    "created_at": "2026-02-27T14:30:00Z",
    "refund_initiated_at": "2026-02-27T14:33:00Z",
    "expected_settlement_at": "2026-02-28T09:00:00Z"
}
```

### **Transactions Table** (Optional - for analytics)

```
Columns:
├─ id (String, Primary Key)
├─ dispute_id (FK to Disputes)
├─ merchant_id (String)
├─ merchant_upi (String)
├─ amount (Float)
├─ failure_category (String)
│  ├─ GATEWAY_TIMEOUT
│  ├─ BANK_DOWN
│  ├─ NETWORK_ERROR
│  ├─ INVALID_UPI
│  ├─ ACCOUNT_INACTIVE
│  └─ UNKNOWN
│
├─ failure_count_merchant (Int)      # How many times this merchant fails
├─ failure_rate (Float)              # % of transactions that fail
└─ created_at (DateTime)
```

### **Merchants Table** (Optional - for dashboard)

```
Columns:
├─ id (String, Primary Key)
├─ merchant_upi (String, Unique)
├─ merchant_name (String)
├─ total_disputes_filed (Int)
├─ disputes_this_month (Int)
├─ failure_rate (Float %)
├─ avg_refund_amount (Float)
├─ health_status (String)
│  ├─ HEALTHY (< 2% failure)
│  ├─ WARNING (2-5% failure)
│  └─ CRITICAL (> 5% failure)
│
├─ last_failure_at (DateTime)
└─ updated_at (DateTime)
```

---

## 🔐 Security & Validation

```
Input Validation:
├─ Transaction ID: Must match format TXN20260227XXXXXX
├─ Merchant UPI: Must be valid UPI format (xxx@bank)
├─ Amount: > 0, < 100,000
├─ Phone: Valid Indian phone format
└─ All inputs sanitized (no SQL injection)

API Rate Limiting:
├─ Per IP: 100 requests per 15 min
├─ Per user: 10 disputes per hour
└─ Prevent abuse

Authentication:
├─ Frontend: No auth needed (public form)
├─ Backend: API key for bank API calls
├─ Refund: Double-check before processing
└─ Notification: Verify phone number with OTP

Error Handling:
├─ Bank API timeout: Retry 3x with exponential backoff
├─ Partial response: Mark for MANUAL_REVIEW
├─ Database error: Log and alert ops
└─ Unknown error: Escalate to manual review
```

---

## ⚡ Performance Targets

```
API Response Times:
├─ POST /api/disputes: < 5 seconds (file dispute)
├─ GET /api/disputes/{id}: < 500ms (check status)
├─ Bank API calls: 2-5 seconds each (parallel)
└─ NEFT initiation: 2-3 seconds

Throughput:
├─ 100 disputes/minute (easy)
├─ Scales to 10,000/minute with load balancing
└─ Database: SQLite fine for hackathon, PostgreSQL for prod

Availability:
├─ 99.9% uptime (no single point of failure)
├─ Health checks every 30 seconds
├─ Automatic failover for bank APIs
└─ Graceful degradation (queue if bank API down)
```

---

## 🎯 Optional Features (If Time Permits)

### **Risk Scoring** (Hours 4-7)

```
Before customer sends money:

Predict risk of failure:
├─ Merchant history (2% failure = baseline)
├─ Transaction time (2:30 AM = high risk)
├─ Amount (₹50,000 = higher risk)
├─ Network (4G = weaker than WiFi)
├─ Device (older phones = slightly higher risk)
├─ Gateway (some gateways more reliable)
├─ Customer history (returning customer = lower risk)
└─ Combinations (4G + 2:30 AM + ₹50K = 65% risk)

Risk Calculation:
├─ Weighted formula (machine learning optional)
├─ Output: 0-100 score
├─ Show: 🟢 LOW (< 25%), 🟡 MEDIUM (25-75%), 🔴 HIGH (> 75%)

Suggest Alternatives:
├─ "Use WiFi instead (risk drops to 10%)"
├─ "Try again at 10 AM (risk drops to 20%)"
├─ "Use NEFT instead (risk 5%, no UPI failure)"
└─ Result: Prevents 40-50% of failures!

API Endpoint:
POST /api/risk-score
{
    "merchant_upi": "amazon@upi",
    "amount": 50000,
    "network": "4G",
    "time": "14:30",
    "device": "iPhone 12"
}
Response:
{
    "risk_score": 65,
    "risk_level": "HIGH",
    "suggestions": [
        {"action": "Use WiFi", "risk_reduction": "-35%"},
        {"action": "Try at 10 AM", "risk_reduction": "-45%"},
        {"action": "Use NEFT", "risk_reduction": "-60%"}
    ]
}
```

### **Merchant Dashboard** (Hours 7-9)

```
For operations team:

Real-time insights:
├─ Total disputes filed: 234
├─ Disputes this month: 45
├─ Merchants with issues:
│  ├─ Amazon UPI: 5.2% failure rate 🔴 CRITICAL
│  ├─ Flipkart UPI: 2.1% failure rate ⚠️ WARNING
│  └─ Ola UPI: 1.2% failure rate ✅ HEALTHY
│
├─ Trends:
│  ├─ Peak failure time: 2-4 AM
│  ├─ Peak failure amount: ₹20K-₹50K
│  └─ Most affected merchants: Amazon, Flipkart
│
└─ Actions:
   ├─ Alert merchants with high failures
   ├─ Escalate to ops for investigation
   └─ Monitor improvement over time

Simple Charts:
├─ Line: Disputes over time
├─ Bar: Disputes by merchant
├─ Line: Failure rate trend
└─ Gauge: System health score

Access: /dashboard (ops login required)
```

---

## 🚀 Next Step

👉 Read **[03_PRODUCT_SPECIFICATION.md](03_PRODUCT_SPECIFICATION.md)** for feature details
