# Mock Bank API - cURL Testing Guide

This guide provides comprehensive cURL commands for testing the Mock Bank API endpoints.

## Prerequisites

- Mock Bank API running on `http://localhost:8080`
- API Key: `upi-dispute-resolver-secret-key-2024`
- cURL installed on your system

## API Key Header

All requests must include the API key header:
```
-H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

---

## 1. CREATE TRANSACTION

### Create a Successful Transaction
```bash
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201001",
    "amount": 1500.00,
    "status": "SUCCESS",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT001",
    "description": "Payment for online shopping"
  }'
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "message": "Transaction created successfully",
  "data": {
    "transaction_id": "TXN20240201001",
    "amount": 1500.00,
    "status": "SUCCESS",
    "timestamp": "2024-02-27T10:45:30.123456",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT001",
    "description": "Payment for online shopping"
  },
  "timestamp": "2024-02-27T10:45:30.123456"
}
```

---

### Create a Failed Transaction
```bash
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201002",
    "amount": 500.00,
    "status": "FAILED",
    "payer_id": "CUST002",
    "payee_id": "MERCHANT002",
    "description": "Payment failed due to insufficient funds"
  }'
```

---

### Create Multiple Test Transactions (Batch)
```bash
# Transaction 1
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201003",
    "amount": 750.00,
    "status": "SUCCESS",
    "payer_id": "CUST003",
    "payee_id": "MERCHANT003",
    "description": "Bill payment"
  }'

# Transaction 2
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201004",
    "amount": 2000.00,
    "status": "SUCCESS",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT004",
    "description": "Utility bill"
  }'

# Transaction 3 (Another failed one)
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201005",
    "amount": 300.00,
    "status": "FAILED",
    "payer_id": "CUST004",
    "payee_id": "MERCHANT005",
    "description": "Transaction timeout"
  }'
```

---

## 2. FETCH TRANSACTION DETAILS

### Fetch Existing Transaction
```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Transaction retrieved successfully",
  "data": {
    "transaction_id": "TXN20240201001",
    "amount": 1500.00,
    "status": "SUCCESS",
    "timestamp": "2024-02-27T10:45:30.123456",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT001",
    "description": "Payment for online shopping"
  },
  "timestamp": "2024-02-27T10:45:35.654321"
}
```

---

### Fetch Non-Existent Transaction
```bash
curl -X GET http://localhost:8080/bank/transaction/TXN99999999999 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

**Expected Response** (404 Not Found):
```json
{
  "success": false,
  "message": "Transaction not found with ID: TXN99999999999",
  "data": null,
  "timestamp": "2024-02-27T10:45:40.123456"
}
```

---

### Fetch the Sample Transactions (Pre-loaded)
```bash
# Original sample transactions
curl -X GET http://localhost:8080/bank/transaction/TXN20240101001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"

curl -X GET http://localhost:8080/bank/transaction/TXN20240101002 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"

curl -X GET http://localhost:8080/bank/transaction/TXN20240101003 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

---

## 3. PROCESS REFUNDS

### Refund a Successful Transaction
```bash
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201001",
    "reason": "Customer requested refund for unsatisfactory service"
  }'
```

**Expected Response** (201 Created):
```json
{
  "success": true,
  "message": "Refund processed successfully",
  "data": {
    "refund_id": 1,
    "transaction_id": "TXN20240201001",
    "amount": 1500.00,
    "refund_timestamp": "2024-02-27T10:50:15.123456",
    "status": "SUCCESS",
    "reason": "Customer requested refund for unsatisfactory service"
  },
  "timestamp": "2024-02-27T10:50:15.123456"
}
```

---

### Try to Refund a Failed Transaction (Should Fail)
```bash
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201002",
    "reason": "Attempting to refund a failed transaction"
  }'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "message": "Refund not allowed for FAILED transactions",
  "data": null,
  "timestamp": "2024-02-27T10:50:20.123456"
}
```

---

### Try to Refund Already Refunded Transaction (Should Fail)
```bash
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240201001",
    "reason": "Attempting double refund"
  }'
```

**Expected Response** (400 Bad Request):
```json
{
  "success": false,
  "message": "Transaction already refunded",
  "data": null,
  "timestamp": "2024-02-27T10:50:25.123456"
}
```

---

### Refund with Sample Transactions
```bash
# Refund first sample transaction
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240101001",
    "reason": "Dispute resolution completed"
  }'

# Try to refund failed sample transaction (should fail)
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "TXN20240101003",
    "reason": "Cannot refund failed transaction"
  }'
```

---

## 4. FETCH ALL REFUNDS

### Get All Refunds
```bash
curl -X GET http://localhost:8080/bank/refunds \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

**Expected Response** (200 OK):
```json
{
  "success": true,
  "message": "Refunds retrieved successfully",
  "data": [
    {
      "refund_id": 1,
      "transaction_id": "TXN20240201001",
      "amount": 1500.00,
      "refund_timestamp": "2024-02-27T10:50:15.123456",
      "status": "SUCCESS",
      "reason": "Customer requested refund for unsatisfactory service"
    },
    {
      "refund_id": 2,
      "transaction_id": "TXN20240101001",
      "amount": 1000.00,
      "refund_timestamp": "2024-02-27T10:52:45.654321",
      "status": "SUCCESS",
      "reason": "Dispute resolution completed"
    }
  ],
  "timestamp": "2024-02-27T10:52:50.123456"
}
```

---

## 5. AUTHENTICATION TESTING

### Missing API Key (Should Fail)
```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240201001
```

**Expected Response** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Missing API key in header: x-api-key",
  "data": null,
  "timestamp": "2024-02-27T10:55:00.123456"
}
```

---

### Invalid API Key (Should Fail)
```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: invalid-api-key-12345"
```

**Expected Response** (401 Unauthorized):
```json
{
  "success": false,
  "message": "Invalid API key",
  "data": null,
  "timestamp": "2024-02-27T10:55:05.123456"
}
```

---

## 6. DATA FORMAT EXAMPLES

### Valid Transaction Status Values
```
SUCCESS - Transaction completed successfully
FAILED  - Transaction failed and cannot be refunded
REFUNDED - Transaction was successfully refunded
```

### Request Body Examples

#### Create Transaction
```json
{
  "transaction_id": "TXN20240201001",
  "amount": 1500.00,
  "status": "SUCCESS",
  "payer_id": "CUST001",
  "payee_id": "MERCHANT001",
  "description": "Payment for services"
}
```

#### Refund Transaction
```json
{
  "transaction_id": "TXN20240201001",
  "reason": "Customer requested refund"
}
```

---

## 7. COMPLETE WORKFLOW EXAMPLE

This shows a complete workflow of creating transactions, fetching details, and processing refunds:

```bash
# Step 1: Create a successful transaction
echo "Step 1: Creating transaction..."
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "WORKFLOW001",
    "amount": 5000.00,
    "status": "SUCCESS",
    "payer_id": "CUST_WORKFLOW",
    "payee_id": "MERCHANT_WORKFLOW",
    "description": "Workflow test transaction"
  }'

echo -e "\n\nStep 2: Fetching transaction details..."
curl -X GET http://localhost:8080/bank/transaction/WORKFLOW001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"

echo -e "\n\nStep 3: Processing refund..."
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "WORKFLOW001",
    "reason": "Refund processed as part of workflow test"
  }'

echo -e "\n\nStep 4: Fetching transaction again (status should be REFUNDED)..."
curl -X GET http://localhost:8080/bank/transaction/WORKFLOW001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"

echo -e "\n\nStep 5: Fetching all refunds..."
curl -X GET http://localhost:8080/bank/refunds \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

---

## 8. PRETTY PRINTING RESPONSES

To format JSON responses nicely, use `jq`:

```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" | jq '.'
```

If `jq` is not installed, use Python:

```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" | python -m json.tool
```

---

## 9. SAVING RESPONSES TO FILES

```bash
# Save transaction response to file
curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" > transaction_response.json

# Save all refunds to file
curl -X GET http://localhost:8080/bank/refunds \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" > all_refunds.json
```

---

## 10. TESTING WITH TIMEOUT

```bash
# Set connection timeout to 5 seconds
curl --max-time 5 http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

---

## 11. TESTING WITH VERBOSE OUTPUT

```bash
# Show request and response headers
curl -v http://localhost:8080/bank/transaction/TXN20240201001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

---

## 12. USEFUL ENDPOINTS FOR DEBUGGING

### Swagger UI (Interactive API Explorer)
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Schema
```
http://localhost:8080/v3/api-docs
```

### H2 Database Console
```
http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:mockbankdb
- Username: sa
- Password: (leave empty)
```

---

## Tips and Tricks

1. **Store API Key in Variable**:
   ```bash
   API_KEY="upi-dispute-resolver-secret-key-2024"
   curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
     -H "x-api-key: $API_KEY"
   ```

2. **Extract Specific Field from Response**:
   ```bash
   curl -X GET http://localhost:8080/bank/transaction/TXN20240201001 \
     -H "x-api-key: upi-dispute-resolver-secret-key-2024" | jq '.data.status'
   ```

3. **Test Multiple Transactions in Loop**:
   ```bash
   for i in {1..5}; do
     curl -X GET http://localhost:8080/bank/transaction/TXN2024010100$i \
       -H "x-api-key: upi-dispute-resolver-secret-key-2024"
     echo ""
   done
   ```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Connection refused | Ensure API is running on `http://localhost:8080` |
| 401 Unauthorized | Check API key header: `upi-dispute-resolver-secret-key-2024` |
| 404 Not Found | Verify transaction ID exists (use sample data) |
| 400 Bad Request | Check JSON format and transaction status |
| 500 Server Error | Check application logs for detailed error messages |

---

## API Key Configuration

To change the API key, edit `src/main/resources/application.yml`:

```yaml
app:
  api-key: "your-new-api-key-here"
```

Then rebuild and restart the application.
