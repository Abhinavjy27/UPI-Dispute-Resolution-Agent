`# Input & Output Formats - Complete Guide

## 📥 INPUTS (What Goes In)

### **1. Dispute Filing (Customer Form)**

**Frontend Form Input:**
```json
{
  "transaction_id": "TXN20260227123456",
  "merchant_name": "Amazon India",
  "merchant_upi": "amazon@upi",
  "amount": 5000,
  "transaction_timestamp": "2026-02-27T14:30:00Z",
  "customer_phone": "+919876543210",
  "customer_name": "Rahul Kumar",
  "network_type": "4G",
  "device_model": "iPhone 13",
  "failure_reason": "Payment timeout - money debited but merchant didn't receive",
  "reference_number": "UPI101234"
}
```

**Sent as POST Request:**
```bash
POST /api/disputes
Content-Type: application/json
{
  "transaction_id": "TXN20260227123456",
  "merchant_upi": "amazon@upi",
  "amount": 5000,
  "customer_phone": "+919876543210",
  "timestamp": "2026-02-27T14:30:00Z"
}
```

---

### **2. Bank Verification Input (Internal API Calls)**

**Customer Bank Query:**
```json
{
  "account_number": "9123456789",
  "transaction_id": "TXN20260227123456",
  "amount": 5000,
  "timestamp": "2026-02-27T14:30:00Z",
  "query": "WAS_DEBITED"
}
```

**Merchant Bank Query:**
```json
{
  "merchant_upi": "amazon@upi",
  "transaction_id": "TXN20260227123456",
  "amount": 5000,
  "timestamp": "2026-02-27T14:30:00Z",
  "query": "WAS_CREDITED"
}
```

---

### **3. Risk Score Calculation Input**

**Transaction Data for Risk Scoring:**
```json
{
  "transaction_id": "TXN20260227123456",
  "merchant_upi": "amazon@upi",
  "amount": 5000,
  "customer_phone": "+919876543210",
  "network_type": "4G",
  "device_age_months": 24,
  "time_of_day": 14,
  "merchant_failure_rate_7d": 0.02,
  "gateway": "HDFC",
  "gateway_load_percent": 45,
  "customer_success_rate": 0.96,
  "customer_transaction_count": 45
}
```

---

## 📤 OUTPUTS (What Comes Out)

### **1. Dispute Filing Response**

**Frontend Gets:**
```json
{
  "success": true,
  "dispute_id": "DIS_1709028600",
  "status": "CREATED",
  "message": "Dispute filed successfully. Verifying with banks...",
  "next_step": "Waiting for bank verification",
  "estimated_time": "5 minutes",
  "reference_number": "DIS_1709028600"
}
```

**What User Sees on Screen:**
```
✅ DISPUTE FILED

Dispute ID: DIS_1709028600
Status: Verifying with banks...
Amount: ₹5,000
Merchant: Amazon India
Expected Timeline: Tomorrow (within 24 hours)

Next Steps:
├─ Bank verification in progress
├─ Auto-decision (usually within 5 minutes)
├─ Refund initiated (same day)
└─ Money in your account (next day)

Tracking Reference: DIS_1709028600
```

---

### **2. Bank Verification Response**

**Customer Bank Returns:**
```json
{
  "account_number": "9123456789",
  "transaction_id": "TXN20260227123456",
  "was_debited": true,
  "amount_debited": 5000,
  "debit_timestamp": "2026-02-27T14:30:05Z",
  "status": "CONFIRMED"
}
```

**Merchant Bank Returns:**
```json
{
  "merchant_upi": "amazon@upi",
  "transaction_id": "TXN20260227123456",
  "was_credited": false,
  "amount_expected": 5000,
  "status": "CONFIRMED",
  "error_code": "TRANSACTION_NOT_FOUND"
}
```

**System Internal Verification Result:**
```json
{
  "dispute_id": "DIS_1709028600",
  "verification_status": "VERIFIED_FAILED",
  "confidence": 100,
  "customer_debited": true,
  "merchant_credited": false,
  "root_cause": "NETWORK_TIMEOUT",
  "decision": "AUTO_APPROVE",
  "reason": "Money clearly left customer account, merchant never received it"
}
```

---

### **3. Risk Score Response**

**Backend Calculates:**
```json
{
  "transaction_id": "TXN20260227123456",
  "risk_score": 0.65,
  "risk_level": "HIGH",
  "risk_percentage": "65%",
  "risk_factors": {
    "merchant_failure_rate": 0.02,
    "time_of_day": 0.15,
    "amount": 0.10,
    "network_type": 0.15,
    "device_age": 0.08,
    "gateway_load": 0.05,
    "customer_success_rate": 0.05,
    "total_weighted": 0.65
  },
  "should_flag": true,
  "alternatives": [
    {
      "name": "Use WiFi instead of 4G",
      "risk_reduction": "45%",
      "new_risk_score": 0.35
    },
    {
      "name": "Try at 10 AM (off-peak)",
      "risk_reduction": "55%",
      "new_risk_score": 0.30
    },
    {
      "name": "Use NEFT instead",
      "risk_reduction": "95%",
      "new_risk_score": 0.03
    }
  ]
}
```

**What User Sees (High Risk):**
```
⚠️ WARNING: This payment might fail (65% risk)

Suggested alternatives (in order of success):
1. ✅ Use NEFT transfer (3% risk) - Takes 2 hours
2. ✅ Try at 10 AM instead (30% risk)
3. ✅ Switch to WiFi (35% risk)

Your choice:
[Use NEFT] [Try at 10 AM] [Switch WiFi] [Proceed Anyway]
```

---

### **4. Auto-Approval Response**

**Backend Decision:**
```json
{
  "dispute_id": "DIS_1709028600",
  "decision": "APPROVED",
  "decision_basis": "VERIFIED_FAILURE",
  "confidence": 100,
  "approval_timestamp": "2026-02-27T14:35:00Z",
  "next_action": "INITIATE_REFUND",
  "refund_details": {
    "amount": 5000,
    "method": "NEFT",
    "customer_account": "9123456789",
    "customer_bank": "HDFC",
    "expected_settlement": "2026-02-28T09:00:00Z"
  }
}
```

---

### **5. NEFT Refund Response**

**Bank API Returns:**
```json
{
  "refund_request_id": "REF_1709028700",
  "status": "INITIATED",
  "amount": 5000,
  "from_account": "FINTECH_ESCROW",
  "to_account": "9123456789",
  "to_bank": "HDFC",
  "reference_number": "DIS_1709028600",
  "utc_reference_number": "NEFT20260227123456",
  "settlement_timestamp": "2026-02-28T09:00:00Z",
  "batch_number": "BATCH_20260227_001"
}
```

**What User Sees:**
```
✅ REFUND INITIATED

Refund Details:
├─ Amount: ₹5,000
├─ Method: NEFT Transfer
├─ Reference: DIS_1709028600
├─ Status: In Process
└─ Expected: Tomorrow 9 AM

Timeline:
├─ Initiated: Feb 27, 2:35 PM
├─ Processing: Feb 27, 2:35 PM - 11:59 PM
├─ Settlement: Feb 28, 9:00 AM
└─ Money in account: Feb 28, 9:00 AM

What happens next:
1. Refund goes to HDFC
2. HDFC processes (overnight)
3. Money appears in your account by 9 AM tomorrow
4. You'll get SMS confirmation
```

---

### **6. Status Tracking Response**

**Customer Checks Status:**
```bash
GET /api/disputes/DIS_1709028600
```

**Backend Returns:**
```json
{
  "dispute_id": "DIS_1709028600",
  "status": "REFUND_INITIATED",
  "current_step": "Processing NEFT transfer",
  "progress": 60,
  "timeline": [
    {
      "step": "Dispute Created",
      "status": "COMPLETED",
      "timestamp": "2026-02-27T14:30:00Z"
    },
    {
      "step": "Bank Verification",
      "status": "COMPLETED",
      "timestamp": "2026-02-27T14:35:00Z"
    },
    {
      "step": "Auto-Approval",
      "status": "COMPLETED",
      "timestamp": "2026-02-27T14:35:30Z"
    },
    {
      "step": "Refund Initiated (NEFT)",
      "status": "IN_PROGRESS",
      "timestamp": "2026-02-27T14:36:00Z",
      "expected_completion": "2026-02-28T09:00:00Z"
    },
    {
      "step": "Refund Settled",
      "status": "PENDING",
      "expected_timestamp": "2026-02-28T09:00:00Z"
    }
  ],
  "refund_reference": "NEFT20260227123456",
  "amount": 5000
}
```

**What User Sees:**
```
📊 DISPUTE STATUS TRACKING

Step 1: ✅ Dispute Filed (Feb 27, 2:30 PM)
Step 2: ✅ Bank Verified (Feb 27, 2:35 PM) 
Step 3: ✅ Auto-Approved (Feb 27, 2:35 PM)
Step 4: 🔄 Refund Processing (In Progress)
Step 5: ⏳ Money in Account (Expected: Feb 28, 9 AM)

Refund Details:
├─ Reference: NEFT20260227123456
├─ Amount: ₹5,000
├─ Status: In NEFT queue
└─ Settlement: Tomorrow morning
```

---

### **7. Merchant Dashboard Output**

**Backend Returns Merchant Health Data:**
```json
{
  "merchants": [
    {
      "merchant_upi": "amazon@upi",
      "merchant_name": "Amazon India",
      "failure_rate": 0.023,
      "failure_rate_trend": "+0.005",
      "status": "⚠️ WATCH",
      "total_transactions_24h": 1245,
      "failures_24h": 28,
      "last_incident": "2026-02-27T14:30:00Z",
      "top_failure_reason": "NETWORK_TIMEOUT"
    },
    {
      "merchant_upi": "flipkart@upi",
      "merchant_name": "Flipkart",
      "failure_rate": 0.008,
      "failure_rate_trend": "-0.002",
      "status": "✅ HEALTHY",
      "total_transactions_24h": 2100,
      "failures_24h": 17,
      "last_incident": "2026-02-27T10:15:00Z",
      "top_failure_reason": "GATEWAY_OVERLOAD"
    }
  ],
  "overall_health": {
    "avg_failure_rate": 0.015,
    "system_status": "NORMAL",
    "at_risk_merchants": 3,
    "healthy_merchants": 47
  }
}
```

**What Dashboard Shows:**
```
MERCHANT HEALTH DASHBOARD

Merchant Name     | Failure Rate | Trend | Status  | Failures (24h)
────────────────────────────────────────────────────────────────────
Amazon India      | 2.3%         | ↑     | ⚠️ WATCH| 28 / 1245
Flipkart          | 0.8%         | ↓     | ✅ OK   | 17 / 2100
Paytm             | 1.2%         | →     | ✅ OK   | 12 / 1000
MakeMyTrip        | 5.6%         | ↑     | 🔴 BAD  | 22 / 400
Uber              | 0.3%         | ↓     | ✅ OK   | 1 / 320

System Status: NORMAL (Avg failure: 1.5%)
```

---

## 🗄️ DATABASE SCHEMA (What Gets Stored)

### **Disputes Table**
```sql
CREATE TABLE disputes (
  dispute_id VARCHAR(50) PRIMARY KEY,
  transaction_id VARCHAR(50) UNIQUE,
  merchant_upi VARCHAR(100),
  customer_phone VARCHAR(20),
  amount DECIMAL(10,2),
  
  status VARCHAR(50),  -- CREATED, VERIFYING, APPROVED, REFUNDING, SETTLED
  verification_status VARCHAR(50),  -- VERIFIED_FAILED, UNCLEAR
  approval_decision VARCHAR(50),  -- APPROVED, REJECTED
  
  customer_debited BOOLEAN,
  merchant_credited BOOLEAN,
  verification_confidence INT,  -- 0-100
  
  neft_reference VARCHAR(50),
  settlement_date DATE,
  
  created_at TIMESTAMP,
  verified_at TIMESTAMP,
  approved_at TIMESTAMP,
  refund_initiated_at TIMESTAMP,
  settled_at TIMESTAMP
);
```

### **Transactions Table**
```sql
CREATE TABLE transactions (
  transaction_id VARCHAR(50) PRIMARY KEY,
  merchant_upi VARCHAR(100),
  customer_phone VARCHAR(20),
  amount DECIMAL(10,2),
  
  network_type VARCHAR(20),  -- 4G, WiFi, 5G
  device_model VARCHAR(100),
  device_age_months INT,
  gateway VARCHAR(50),  -- HDFC, Axis, ICICI
  timestamp TIMESTAMP,
  
  success BOOLEAN,
  failure_reason VARCHAR(200),  -- TIMEOUT, OVERLOAD, INVALID_UPI
  response_time_ms INT,
  
  created_at TIMESTAMP
);
```

### **Merchants Table**
```sql
CREATE TABLE merchants (
  merchant_upi VARCHAR(100) PRIMARY KEY,
  merchant_name VARCHAR(200),
  failure_rate_1d DECIMAL(5,3),
  failure_rate_7d DECIMAL(5,3),
  total_tx_1d INT,
  failures_1d INT,
  status VARCHAR(50),  -- HEALTHY, WATCH, CRITICAL
  last_incident TIMESTAMP,
  updated_at TIMESTAMP
);
```

---

## 🔄 Real-World Example (Step-by-Step)

### **Customer's Journey (Input → Processing → Output)**

**Step 1: Customer Submits Form (INPUT)**
```json
{
  "transaction_id": "UPI20260227001",
  "merchant_upi": "amazon@upi",
  "amount": 5000,
  "customer_phone": "+919876543210"
}
```

**Step 2: System Processes (INTERNAL)**
- Calls customer bank API
- Calls merchant bank API
- Calculates risk score
- Makes approval decision
- Initiates NEFT refund
- Sends SMS notification

**Step 3: Customer Sees (OUTPUT)**
```
✅ REFUND INITIATED
Reference: DIS_1709028600
Amount: ₹5,000
Timeline: Tomorrow 9 AM
```

---

## 📋 API Endpoint Summary

| Endpoint | Method | Input | Output |
|----------|--------|-------|--------|
| `/api/disputes` | POST | Dispute form data | Dispute ID, status |
| `/api/disputes/{id}` | GET | Dispute ID | Full dispute status |
| `/api/risk-score` | POST | Transaction data | Risk score, alternatives |
| `/api/merchant-health` | GET | - | All merchants' health |
| `/api/refund-status/{id}` | GET | Dispute ID | NEFT status |

---

## 💾 Sample Data Files

### **Mock Bank Responses** `mock_bank_api.json`
```json
{
  "customer_bank_response": {
    "account": "9123456789",
    "transaction_id": "UPI20260227001",
    "was_debited": true,
    "amount": 5000
  },
  "merchant_bank_response": {
    "merchant_upi": "amazon@upi",
    "transaction_id": "UPI20260227001",
    "was_credited": false,
    "error": "TRANSACTION_NOT_FOUND"
  }
}
```

### **Test Disputes** `test_disputes.json`
```json
[
  {
    "transaction_id": "TEST001",
    "merchant_upi": "flipkart@upi",
    "amount": 2500,
    "customer_phone": "+919999999999",
    "expected_outcome": "APPROVED"
  },
  {
    "transaction_id": "TEST002",
    "merchant_upi": "invalid@upi",
    "amount": 10000,
    "customer_phone": "+918888888888",
    "expected_outcome": "ESCALATED"
  }
]
```

---

**This format helps you:**
- ✅ Build consistent APIs
- ✅ Test with real data
- ✅ Understand data flow
- ✅ Mock responses during development
