# Mock Bank API

Simple mock server that simulates customer and merchant bank verification for UPI disputes.

## 🚀 Quick Start

```bash
# Install dependencies
npm install

# Run the mock API
npm start
```

Server runs on **http://localhost:9000**

## 📡 Endpoints

### 1. Unified Verification (Recommended)

**POST** `/mock-bank/verify`

**Request:**
```json
{
  "type": "CUSTOMER",
  "transactionId": "TXN20260227123456",
  "accountNumber": "9123456789",
  "amount": 5000
}
```

**Response (Customer):**
```json
{
  "success": true,
  "verified": true,
  "debited": true,
  "amount": 5000,
  "debitTimestamp": "2026-02-27T14:30:00Z"
}
```

**Request (Merchant):**
```json
{
  "type": "MERCHANT",
  "transactionId": "TXN20260227123456",
  "merchantUpi": "amazon@upi",
  "amount": 5000
}
```

**Response (Merchant):**
```json
{
  "success": true,
  "verified": true,
  "credited": false,
  "amount": 0,
  "status": "TRANSACTION_NOT_FOUND"
}
```

### 2. Separate Endpoints (Alternative)

**POST** `/customer-bank/verify`
- Verifies if money was debited from customer account

**POST** `/merchant-bank/verify`
- Verifies if money was credited to merchant account

### 3. Health Check

**GET** `/health`

## 🧪 Test with cURL

```bash
# Test customer verification
curl -X POST http://localhost:9000/mock-bank/verify \
  -H "Content-Type: application/json" \
  -d '{
    "type": "CUSTOMER",
    "transactionId": "TXN20260227123456",
    "accountNumber": "9123456789",
    "amount": 5000
  }'

# Test merchant verification
curl -X POST http://localhost:9000/mock-bank/verify \
  -H "Content-Type: application/json" \
  -d '{
    "type": "MERCHANT",
    "transactionId": "TXN20260227123456",
    "merchantUpi": "amazon@upi",
    "amount": 5000
  }'
```

## 📝 How It Works

1. **Always returns "failure scenario"** for demo:
   - Customer bank: Money WAS debited ✅
   - Merchant bank: Money was NOT credited ❌
   - This triggers auto-approval for refund

2. **Simulates 1 second delay** to mimic real bank API latency

3. **Simple to modify** - edit response in `mock-bank-api.js` for different scenarios

## 🔧 Customization

Edit `mock-bank-api.js` to change:
- Response delay (currently 1000ms)
- Return different scenarios (success, escalation, etc.)
- Add authentication if needed

## 📦 Integration

Your backend should call:

```javascript
// Example backend code
const customerVerified = await axios.post('http://localhost:9000/mock-bank/verify', {
  type: 'CUSTOMER',
  transactionId: dispute.transactionId,
  amount: dispute.amount
});

const merchantVerified = await axios.post('http://localhost:9000/mock-bank/verify', {
  type: 'MERCHANT',
  transactionId: dispute.transactionId,
  merchantUpi: dispute.merchantUpi,
  amount: dispute.amount
});

// Decision logic
if (customerVerified.data.debited && !merchantVerified.data.credited) {
  // Auto-approve refund!
}
```

## ⚙️ Environment Variables

```bash
PORT=9000  # Default port
```

## 🎯 For Production

Replace this mock with real bank API integrations:
- NPCI verification APIs
- Bank-specific APIs
- UPI transaction query services

---

**Note:** This is a MOCK for development/demo. Do not use in production!
