# 03_PRODUCT_SPECIFICATION

## 📋 Feature Breakdown

### **CORE FEATURES (Must Build - Hours 0-6)**

#### **Feature 1: Dispute Filing Form**

```
User Interface:
┌─────────────────────────────────────┐
│  Resolve Your UPI Failure in 24h     │
├─────────────────────────────────────┤
│                                     │
│  Transaction ID *                  │
│  [TXN20260227123456____________] │
│                                     │
│  Merchant UPI *                    │
│  [amazon@upi__________________]    │
│                                     │
│  Amount (₹) *                      │
│  [5000____________________]         │
│                                     │
│  Your Phone *                      │
│  [+91 9876543210__________]        │
│                                     │
│  [   SUBMIT   ]                     │
│                                     │
└─────────────────────────────────────┘

Fields:
├─ Transaction ID (required)
│  └─ Validation: TXN followed by 14+ digits
│  └─ Help text: "Find in your transaction history"
│
├─ Merchant UPI (required)
│  └─ Validation: xx@upi or xx@bank format
│  └─ Help text: "Ask merchant or check receipt"
│
├─ Amount (required)
│  └─ Validation: ₹1 - ₹100,000
│  └─ Help text: "Amount that failed"
│
└─ Phone (required)
   └─ Validation: +91 followed by 10 digits
   └─ Help text: "We'll SMS updates here"

Functionality:
├─ Real-time validation (show error if format wrong)
├─ Autocomplete merchant UPI (from recent transactions)
├─ Disable submit if validation fails
└─ Loading state while submitting
```

**Submission Flow:**

```
1. User enters data
   ├─ Form validates
   └─ Submit button enabled
   
2. User clicks SUBMIT
   ├─ Button shows "Processing..."
   ├─ Spinner animation
   └─ Disable other buttons
   
3. Backend verifies in 5-10 seconds
   ├─ Check with customer bank
   ├─ Check with merchant bank
   ├─ Make decision
   └─ Initiate refund
   
4. Success response
   ├─ Show confirmation screen
   ├─ Dispute ID: DIS_1709028600
   ├─ Message: "Refund initiated!"
   ├─ NEFT Reference: NEFT20260227123456
   ├─ Next steps: "Check SMS in 24h"
   └─ Track button: "View Status"
```

**Success Screen:**

```
┌─────────────────────────────────────┐
│  ✅ Refund Initiated!                │
├─────────────────────────────────────┤
│                                     │
│  Your refund of ₹5,000 is on       │
│  the way. Money will reach by      │
│  tomorrow 9 AM.                     │
│                                     │
│  Dispute ID:  DIS_1709028600        │
│  NEFT Ref:    NEFT20260227123456    │
│  Amount:      ₹5,000                │
│                                     │
│  ✔ We've verified the failure      │
│  ✔ Refund auto-approved            │
│  ✔ Initiated with your bank        │
│                                     │
│  [ View Status ]  [ Back to Form ]  │
│                                     │
└─────────────────────────────────────┘
```

**Error Handling:**

```
Case 1: Money already reached merchant
┌─────────────────────────────────────┐
│  ❌ Claim Rejected                   │
├─────────────────────────────────────┤
│  We found that ₹5,000 was already  │
│  received by amazon@upi             │
│                                     │
│  Money is not stuck. No refund.     │
│  If money missing, contact merchant.│
│  [ Contact Support ]                │
└─────────────────────────────────────┘

Case 2: Unable to verify (edge case)
┌─────────────────────────────────────┐
│  ⏳ Manual Review                    │
├─────────────────────────────────────┤
│  This case needs manual review.     │
│  Our team will investigate within   │
│  24-48 hours.                       │
│                                     │
│  Reference: DIS_1709028600          │
│  We'll SMS updates.                 │
│  [ Track Here ]                     │
└─────────────────────────────────────┘
```

**Technical Requirements:**

```
Frontend (React + Tailwind):
├─ Input validation (real-time feedback)
├─ Format checking with regex
├─ Loading states (button spinner)
├─ Error message display
├─ Success message with multi-line text
├─ Auto-focus on first field
├─ Keyboard support (Tab to navigate)
├─ Mobile responsive (100% width on mobile)
└─ Copy-to-clipboard for NEFT reference

Backend (FastAPI):
├─ POST /api/disputes endpoint
├─ Input validation (Pydantic models)
├─ Database insert (status = PENDING)
├─ Async bank API calls (parallel)
├─ Decision logic
├─ NEFT initiation
├─ Response with dispute_id + neft_reference
└─ Error handling with proper HTTP codes
```

---

#### **Feature 2: Bank API Verification**

```
Two Bank APIs to call:

API 1: Customer Bank
POST https://customer-bank.api/verify-debit
{
    "transaction_id": "TXN20260227123456",
    "account_id": "xxxxxxxx1234",
    "query_type": "debit_verification"
}

Response:
{
    "success": true,
    "debited": true,
    "amount": 5000,
    "timestamp": "2026-02-27T14:30:00Z"
}

API 2: Merchant Bank
POST https://merchant-bank.api/verify-credit
{
    "merchant_upi": "amazon@upi",
    "query_type": "credit_verification",
    "amount": 5000,
    "timestamp_from": "2026-02-27T14:25:00Z",
    "timestamp_to": "2026-02-27T14:35:00Z"
}

Response:
{
    "success": true,
    "credited": false,
    "received_amount": 0,
    "details": "UPI timeout - funds never reached"
}

Implementation:
├─ Make both calls in parallel (asyncio)
├─ Timeout: 5 seconds per call
├─ Retry logic: 3 attempts with backoff
├─ Cache results for 24 hours
├─ Log all calls for audit trail
└─ Alert if one bank API is unreachable
```

---

#### **Feature 3: Status Tracking**

```
Endpoint: GET /api/disputes/{dispute_id}

Response:
{
    "dispute_id": "DIS_1709028600",
    "status": "REFUND_INITIATED",
    "amount": 5000,
    "merchant_upi": "amazon@upi",
    "created_at": "2026-02-27T14:30:00Z",
    "verified_at": "2026-02-27T14:32:00Z",
    "refund_initiated_at": "2026-02-27T14:33:00Z",
    "expected_settlement_at": "2026-02-28T09:00:00Z",
    "neft_reference": "NEFT20260227123456",
    "message": "Your refund is being processed. Money by tomorrow 9 AM."
}

Status Progression:
├─ PENDING (just filed)
├─ VERIFYING (checking banks)
├─ VERIFIED_FAILURE (clear failure confirmed)
├─ REFUND_INITIATED (NEFT sent)
├─ REFUND_SETTLED (money reached customer)
└─ CLOSED (complete)

UI Status Page:
┌─────────────────────────────────────┐
│  Refund Status                      │
├─────────────────────────────────────┤
│                                     │
│  Dispute ID:    DIS_1709028600      │
│  Status:        REFUND_INITIATED ⏳  │
│  Filed:         Feb 27, 2:30 PM    │
│  Verified:      Feb 27, 2:32 PM    │
│  Amount:        ₹5,000              │
│  NEFT Ref:      NEFT20260227123456  │
│                                     │
│  Timeline:                          │
│  ✓ Failure verified                │
│  ⏳ NEFT processing                 │
│  ⏳ Money arriving (24-48h)         │
│                                     │
│  Expected by: Feb 28, 9:00 AM      │
│                                     │
│  [ Refresh ]  [ Download Receipt ]  │
│                                     │
└─────────────────────────────────────┘
```

---

### **OPTIONAL FEATURES (Nice to Have - Hours 6-10)**

#### **Feature 4: Risk Score (Prevention)**

```
Goal: Show customer if transaction will likely fail BEFORE they send money

User Journey:
1. Customer enters merchant UPI and amount
2. System instantly calculates risk
3. Show risk badge: 🟢 LOW (20%) | 🟡 MEDIUM (50%) | 🔴 HIGH (75%)
4. If HIGH risk, suggest alternatives
5. Customer chooses safe option

Risk Calculation Model:

Factors:
├─ [1] Merchant Failure History (baseline)
│     ├─ Amazon: 5.2% failure rate
│     ├─ Flipkart: 2.1% failure rate
│     └─ Unknown merchants: 1.5%
│
├─ [2] Transaction Amount
│     ├─ < ₹1,000: Very reliable
│     ├─ ₹1K-₹10K: Normal risk
│     ├─ ₹10K-₹50K: Medium risk
│     └─ > ₹50K: High risk
│
├─ [3] Time of Day
│     ├─ 10 AM - 4 PM: Low risk (peak hours, tested)
│     ├─ 4 PM - 10 PM: Normal risk
│     ├─ 10 PM - 2 AM: Medium risk (lower volume)
│     └─ 2 AM - 10 AM: High risk (maintenance windows)
│
├─ [4] Network Type
│     ├─ WiFi: Very reliable (5-click payment)
│     ├─ 5G: Reliable
│     ├─ 4G: Normal
│     └─ 3G/2G: High risk (packet loss)
│
├─ [5] Device Type
│     ├─ iPhone 12+: Very reliable
│     ├─ Pixel 7+: Very reliable
│     ├─ Recent Android: Normal
│     └─ Older devices: Medium risk
│
├─ [6] Gateway Reliability
│     ├─ NPCI Primary: 99.2% success
│     ├─ Gateway A: 96% success
│     ├─ Gateway B: 92% success
│     └─ Gateway C: 85% success
│
├─ [7] Customer History
│     ├─ 100+  successful txns: Lower risk
│     ├─ 10-99 successful txns: Normal risk
│     └─ 1-9   successful txns: Higher risk
│
└─ [8] Combinations (interaction effects)
      └─ 4G + 2:30 AM + ₹50K = VERY HIGH RISK

Formula:
risk_score = weighted_sum([factors]) with interactions
├─ Each factor weighted differently
├─ Combinations checked (4G at 2 AM = extra penalty)
└─ Machine learning optional (gradient boosting model)

Suggestions:
Rule-based suggestions if HIGH risk:
├─ "Switch to WiFi" → Risk drops 20-30%
├─ "Try at 10 AM" → Risk drops 30-40%
├─ "Use smaller amount" → Risk drops proportionally
├─ "Use NEFT instead" → Risk drops to <5% (slow but sure)
└─ "Try different merchant payment method" → Avoid UPI

Example Output:
{
    "merchant_upi": "amazon@upi",
    "amount": 50000,
    "network": "4G",
    "time": "2030",
    "device": "Samsung A10",
    "risk_score": 72,
    "risk_level": "HIGH",
    "risk_factors": {
        "merchant_history": "5.2%",
        "amount": "HIGH (>₹50K)",
        "time": "Night (higher failure)",
        "network": "4G (lower reliability)"
    },
    "suggestions": [
        {
            "action": "Use WiFi instead",
            "new_risk_score": 35,
            "risk_reduction": "-37%"
        },
        {
            "action": "Try at 10 AM",
            "new_risk_score": 28,
            "risk_reduction": "-44%"
        },
        {
            "action": "Split into multiple ₹20K txns",
            "new_risk_score": 45,
            "risk_reduction": "-27%"
        },
        {
            "action": "Use NEFT (slower but safe)",
            "new_risk_score": 5,
            "risk_reduction": "-67%"
        }
    ]
}

Frontend Display:
┌─────────────────────────────────────┐
│  Risk Assessment for This Payment   │
├─────────────────────────────────────┤
│                                     │
│  Risk Level: 🔴 HIGH (72%)          │
│                                     │
│  Why risky?                         │
│  ├─ 4G network (weaker)            │
│  ├─ Late night (2:30 AM)           │
│  ├─ Large amount (₹50K)            │
│  └─ Amazon has 5% failure rate     │
│                                     │
│  Make it safer:                     │
│  ├─ Switch to WiFi: 35% risk      │
│  ├─ Try at 10 AM: 28% risk        │
│  └─ Use NEFT: 5% risk (slower)     │
│                                     │
│  [ Continue anyway ] [ Use WiFi ]   │
│                                     │
└─────────────────────────────────────┘
```

**Implementation Priority:**
- Start: Simple rule-based model (if-else)
- If time: Upgrade to ML model (sklearn)
- Never: Over-engineer (keep it simple enough to test in 3 hours)

---

#### **Feature 5: Merchant Dashboard**

```
Purpose: Ops team sees which merchants have issues

Access: /dashboard (internal only)

Main Metrics:
├─ Total disputes filed (this month): 45
├─ Total refunded (this month): ₹2,25,000
├─ Merchants with highest failures: [list]
└─ System health: 94.2% ✅

Merchant List with Health:

┌─────────────────────────────────────────────────┐
│ Merchant      │ Failures │ Rate  │ Status    │  │
├─────────────────────────────────────────────────┤
│ Amazon UPI    │   45    │ 5.2%  │ 🔴 CRITICAL │
│ Flipkart UPI  │   12    │ 2.1%  │ ⚠️ WARNING  │
│ Ola UPI       │    8    │ 1.2%  │ ✅ HEALTHY  │
│ Swiggy UPI    │    5    │ 0.8%  │ ✅ HEALTHY  │
└─────────────────────────────────────────────────┘

Click merchant → See details:
├─ Failure rate trend (line chart)
├─ Disputes by hour (heatmap)
├─ Common failure reasons (bar chart)
├─ Average refund amount
└─ Recent disputes list

Charts:
├─ Disputes over time (line chart)
├─ Disputes by merchant (horizontal bar)
├─ Failure rate by time of day (heatmap)
├─ Top merchants by dispute count (pie)
└─ System status gauge (0-100)
```

---

## 🎯 MVP Scope (What YOU Must Build)

### **Hours 0-6: Core System**

```
MUST HAVE:
├─ [✓] React form with 4 fields
├─ [✓] FastAPI backend with 2 endpoints
├─ [✓] SQLite database with disputes table
├─ [✓] Bank API verification (parallel calls)
├─ [✓] Auto-approval logic
├─ [✓] NEFT refund initiation
├─ [✓] Status tracking UI
├─ [✓] SMS notification (or console log for demo)
└─ [✓] Error handling (false claims, etc.)

Success Criteria:
├─ Form submits correctly
├─ Backend processes in < 10 seconds
├─ Decision is correct (VERIFIED_FAILURE detected)
├─ Status page shows refund initiated
└─ User gets NEFT reference number
```

### **Hours 6-10: Optional Features**

```
NICE TO HAVE (order of priority):
├─ [1] Risk Score (3 hours) - HIGH IMPACT
│      └─ Shows risk on form, suggests alternatives
│
├─ [2] Merchant Dashboard (2 hours) - GOOD TO DEMO
│      └─ Simple charts showing merchant failures
│
└─ [3] Root Cause Analysis (1 hour) - BONUS
       └─ Classify why each failure happened
```

### **Hours 10-12: Polish & Demo**

```
NOT FEATURES, BUT ESSENTIAL:
├─ [✓] UI polish (colors, spacing, fonts)
├─ [✓] Test data (5 sample disputes)
├─ [✓] Demo script (what to show judges)
├─ [✓] Presentation pitch (60 seconds)
├─ [✓] Handle network errors gracefully
├─ [✓] Make sure buttons are clickable
└─ [✓] Check mobile responsiveness
```

---

## 📱 User Journey Map

```
NORMAL PATH (Happy case - 95% of users):

1. User visits app
   └─ Sees: "Resolve your UPI failure here"

2. User fills form
   ├─ Transaction ID
   ├─ Merchant UPI
   ├─ Amount
   └─ Phone number

3. User clicks SUBMIT
   └─ Shows: "Verifying with banks..."

4. Backend checks banks (5-10 sec)
   ├─ Bank 1: Money left customer ✓
   └─ Bank 2: Money didn't reach merchant ✗

5. System approves refund
   └─ Shows: "✅ Refund initiated!"

6. User sees confirmation
   ├─ Dispute ID
   ├─ NEFT Reference
   ├─ Expected settlement: Tomorrow 9 AM
   └─ Message: "You'll get SMS when settled"

7. User can track status anytime
   └─ Clicks "View Status" → See REFUND_INITIATED

8. Next day 9 AM
   ├─ Money settles in customer account
   ├─ Status updated to REFUND_SETTLED
   └─ SMS confirmation sent


EDGE CASES (5% of users):

Path A: False Claim (merchant received money)
├─ Banks verify: Money DID reach merchant
├─ System rejects: "Money was received"
└─ Next steps: Contact merchant for issue resolution

Path B: Unclear Case (system can't decide)
├─ Banks give conflicting info
├─ System escalates: MANUAL_REVIEW
├─ Message: "Our team will check within 24h"
└─ Status: Waiting for ops team decision
```

---

## ✅ Testing Checklist

```
Before demo:

CORE FUNCTIONALITY:
├─ [ ] Form accepts all 4 inputs
├─ [ ] Form validates input format
├─ [ ] Submit sends request to backend
├─ [ ] Backend calls both bank APIs
├─ [ ] Decision is correct
├─ [ ] Refund is initiated
├─ [ ] Status page shows refund details
├─ [ ] User can see NEFT reference

UI/UX:
├─ [ ] Form looks clean
├─ [ ] Success page has clear message
├─ [ ] Buttons are clickable
├─ [ ] Loading state shows during processing
├─ [ ] Mobile view works
├─ [ ] Error messages are clear

DATA:
├─ [ ] Dispute saved to database
├─ [ ] Bank API responses logged
├─ [ ] Status updates work
├─ [ ] Can query by dispute ID

EDGE CASES:
├─ [ ] Test with false claim (rejected)
├─ [ ] Test with invalid input (error shown)
├─ [ ] Test with network timeout
├─ [ ] Test on slow connection
└─ [ ] Multiple submissions work correctly

DEMO-READY:
├─ [ ] Have 3 test transactions ready
├─ [ ] Practice demo (2 min walkthrough)
├─ [ ] Know what to say about each feature
├─ [ ] Have backup test data
└─ [ ] Test form twice before presenting
```

---

## 🚀 Next Step

👉 Read **[04_API_CONTRACTS.md](04_API_CONTRACTS.md)** for exact API specifications
