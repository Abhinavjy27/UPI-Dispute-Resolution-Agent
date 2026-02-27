# Mock Bank API - Integration Guide with Python Dispute Resolution Agent

## Overview

The Mock Bank API serves as the backend service for the UPI Dispute Resolution Agent. The Python-based agent uses this API to verify failed transactions and trigger refunds based on dispute resolution outcomes.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                    UPI Dispute Resolution System                    │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│   Python Dispute Resolution Agent            │
│   ├── Transaction Verification Logic         │
│   ├── Dispute Analysis Engine               │
│   ├── Refund Decision Engine                │
│   └── Status Tracking                        │
└──────────────┬───────────────────────────────┘
               │
        REST API Calls
               │
       ┌───────┴──────────┐
       │                  │
       ▼                  ▼
   GET Request        POST Request
   /bank/transaction  /bank/refund
       │                  │
┌──────────────────────────────────────────────┐
│   Mock Bank API (Java Spring Boot)           │
│   ├── Transaction Service                   │
│   ├── Refund Service                        │
│   ├── H2 Database                           │
│   └── API Key Authentication                │
└──────────────────────────────────────────────┘
       │                  │
       └──────┬───────────┘
              │
       ┌──────────────────┐
       │   H2 Database    │
       │   Tables:        │
       │  - transactions  │
       │  - refunds       │
       └──────────────────┘
```

## Integration Flow

### Phase 1: Transaction Verification

```
Python Agent Flow:
1. Receive transaction failure notification
   └─> transactionId: "TXN20240101001"
   
2. Call Mock Bank API to get transaction details
   └─> GET /bank/transaction/TXN20240101001
   
3. Mock Bank API Response:
   {
     "success": true,
     "data": {
       "transaction_id": "TXN20240101001",
       "amount": 1000.00,
       "status": "SUCCESS",
       "payer_id": "CUST001",
       "payee_id": "MERCHANT001",
       "timestamp": "2024-01-01T10:30:00"
     }
   }
   
4. Python Agent analyzes:
   ✓ Check if transaction exists and status
   ✓ Verify amount and participants
   ✓ Cross-check with merchant records
   ✓ Apply dispute resolution rules
```

### Phase 2: Refund Processing

```
Python Agent Flow:
1. Dispute resolution completed
   └─> Decision: "APPROVE REFUND"
   
2. Call Mock Bank API to process refund
   └─> POST /bank/refund
       {
         "transaction_id": "TXN20240101001",
         "reason": "Dispute resolved - customer verified legitimate claim"
       }
   
3. Mock Bank API Processing:
   ✓ Verify transaction exists
   ✓ Check transaction status is SUCCESS
   ✓ Create refund record
   ✓ Update transaction status to REFUNDED
   
4. Mock Bank API Response:
   {
     "success": true,
     "data": {
       "refund_id": 1,
       "transaction_id": "TXN20240101001",
       "amount": 1000.00,
       "status": "SUCCESS",
       "refund_timestamp": "2024-01-01T11:00:00"
     }
   }
   
5. Python Agent:
   ✓ Log refund confirmation
   ✓ Update dispute record as resolved
   ✓ Notify stakeholders
```

## Python Integration Code Example

### Python Dependencies
```python
import requests
import json
from datetime import datetime

# Configuration
BANK_API_BASE_URL = "http://localhost:8080"
API_KEY = "upi-dispute-resolver-secret-key-2024"

headers = {
    "Content-Type": "application/json",
    "x-api-key": API_KEY
}
```

### 1. Fetch Transaction Details

```python
def get_transaction_details(transaction_id):
    """
    Fetch transaction details from Mock Bank API
    
    Args:
        transaction_id (str): The transaction ID to fetch
        
    Returns:
        dict: Transaction details or error response
    """
    try:
        url = f"{BANK_API_BASE_URL}/bank/transaction/{transaction_id}"
        response = requests.get(url, headers=headers, timeout=5)
        
        if response.status_code == 200:
            data = response.json()
            if data['success']:
                return {
                    'status': 'success',
                    'transaction': data['data']
                }
            else:
                return {
                    'status': 'error',
                    'message': data['message']
                }
        elif response.status_code == 404:
            return {
                'status': 'error',
                'message': 'Transaction not found'
            }
        else:
            return {
                'status': 'error',
                'message': f'API Error: {response.status_code}'
            }
            
    except requests.exceptions.RequestException as e:
        return {
            'status': 'error',
            'message': f'Connection error: {str(e)}'
        }

# Usage
result = get_transaction_details("TXN20240101001")
if result['status'] == 'success':
    transaction = result['transaction']
    print(f"Transaction: {transaction['transaction_id']}")
    print(f"Amount: {transaction['amount']}")
    print(f"Status: {transaction['status']}")
else:
    print(f"Error: {result['message']}")
```

### 2. Process Refund

```python
def process_refund(transaction_id, reason):
    """
    Process refund through Mock Bank API
    
    Args:
        transaction_id (str): The transaction ID to refund
        reason (str): Reason for refund
        
    Returns:
        dict: Refund confirmation or error response
    """
    try:
        url = f"{BANK_API_BASE_URL}/bank/refund"
        payload = {
            "transaction_id": transaction_id,
            "reason": reason
        }
        
        response = requests.post(url, headers=headers, json=payload, timeout=5)
        
        if response.status_code == 201:
            data = response.json()
            if data['success']:
                return {
                    'status': 'success',
                    'refund': data['data']
                }
            else:
                return {
                    'status': 'error',
                    'message': data['message']
                }
        elif response.status_code == 400:
            return {
                'status': 'error',
                'message': 'Refund not allowed (Transaction is FAILED or already REFUNDED)'
            }
        elif response.status_code == 404:
            return {
                'status': 'error',
                'message': 'Transaction not found'
            }
        else:
            return {
                'status': 'error',
                'message': f'API Error: {response.status_code}'
            }
            
    except requests.exceptions.RequestException as e:
        return {
            'status': 'error',
            'message': f'Connection error: {str(e)}'
        }

# Usage
result = process_refund(
    "TXN20240101001",
    "Customer dispute resolved - legitimate refund claim"
)

if result['status'] == 'success':
    refund = result['refund']
    print(f"Refund processed successfully")
    print(f"Refund ID: {refund['refund_id']}")
    print(f"Amount: {refund['amount']}")
    print(f"Status: {refund['status']}")
else:
    print(f"Refund failed: {result['message']}")
```

### 3. Complete Dispute Resolution Workflow

```python
def resolve_dispute(transaction_id, dispute_data):
    """
    Complete workflow: Verify transaction and process refund if valid
    
    Args:
        transaction_id (str): The transaction to resolve
        dispute_data (dict): Dispute information
        
    Returns:
        dict: Resolution result
    """
    print(f"[Dispute Resolution] Processing transaction: {transaction_id}")
    
    # Step 1: Fetch transaction details
    print(f"[Step 1] Fetching transaction details...")
    txn_result = get_transaction_details(transaction_id)
    
    if txn_result['status'] != 'success':
        print(f"[ERROR] Failed to fetch transaction: {txn_result['message']}")
        return {
            'success': False,
            'message': 'Transaction verification failed'
        }
    
    transaction = txn_result['transaction']
    print(f"[Step 1] Transaction found: {transaction['transaction_id']}")
    print(f"         Amount: {transaction['amount']}")
    print(f"         Status: {transaction['status']}")
    
    # Step 2: Verify transaction eligibility for refund
    print(f"\n[Step 2] Verifying refund eligibility...")
    
    if transaction['status'] == 'FAILED':
        print(f"[WARNING] Transaction is already FAILED - refund not allowed")
        return {
            'success': False,
            'message': 'Refund not allowed for FAILED transactions'
        }
    
    if transaction['status'] == 'REFUNDED':
        print(f"[WARNING] Transaction already REFUNDED")
        return {
            'success': False,
            'message': 'Transaction already refunded'
        }
    
    print(f"[Step 2] Transaction is eligible for refund")
    
    # Step 3: Apply dispute resolution logic
    print(f"\n[Step 3] Applying dispute resolution rules...")
    
    # Your custom logic here
    approval_reason = dispute_data.get(
        'reason',
        'Customer initiated dispute - verified legitimate claim'
    )
    
    print(f"[Step 3] Dispute approved: {approval_reason}")
    
    # Step 4: Process refund
    print(f"\n[Step 4] Processing refund...")
    refund_result = process_refund(transaction_id, approval_reason)
    
    if refund_result['status'] != 'success':
        print(f"[ERROR] Refund processing failed: {refund_result['message']}")
        return {
            'success': False,
            'message': 'Refund processing failed'
        }
    
    refund = refund_result['refund']
    print(f"[Step 4] Refund processed successfully")
    print(f"         Refund ID: {refund['refund_id']}")
    print(f"         Amount: {refund['amount']}")
    
    # Step 5: Return resolution summary
    print(f"\n[COMPLETED] Dispute resolution completed successfully")
    
    return {
        'success': True,
        'transaction': transaction,
        'refund': refund,
        'timestamp': datetime.now().isoformat()
    }

# Usage
dispute_info = {
    'reason': 'Customer reported unauthorized transaction'
}

result = resolve_dispute("TXN20240101001", dispute_info)

if result['success']:
    print("\n✅ Dispute resolved successfully")
    print(f"   Transaction: {result['transaction']['transaction_id']}")
    print(f"   Refund Amount: {result['refund']['amount']}")
else:
    print("\n❌ Dispute resolution failed")
    print(f"   Error: {result['message']}")
```

### 4. Error Handling and Retry Logic

```python
from time import sleep

def process_with_retry(transaction_id, reason, max_retries=3):
    """
    Process refund with retry logic
    """
    for attempt in range(1, max_retries + 1):
        try:
            print(f"[Attempt {attempt}/{max_retries}] Processing refund...")
            result = process_refund(transaction_id, reason)
            
            if result['status'] == 'success':
                print(f"✅ Refund successful")
                return result
            else:
                print(f"⚠️  Error: {result['message']}")
                
                if attempt < max_retries:
                    wait_time = 2 ** attempt  # Exponential backoff
                    print(f"   Retrying in {wait_time} seconds...")
                    sleep(wait_time)
                    
        except Exception as e:
            print(f"❌ Exception on attempt {attempt}: {str(e)}")
            
            if attempt < max_retries:
                wait_time = 2 ** attempt
                print(f"   Retrying in {wait_time} seconds...")
                sleep(wait_time)
    
    print(f"❌ Failed after {max_retries} attempts")
    return {
        'status': 'error',
        'message': f'Failed after {max_retries} retry attempts'
    }
```

## Data Flow Diagrams

### Successful Refund Flow
```
Python Agent                          Mock Bank API
    │                                      │
    ├──────────────────────────────────────>│
    │   GET /bank/transaction/TXN001       │
    │                                      ├─ Lookup transaction in DB
    │   <──────────────────────────────────┤
    │   {success: true, data: {...}}       │
    │                                      │
    ├─ Verify transaction details         │
    ├─ Check eligible for refund          │
    ├─ Approve dispute resolution         │
    │                                      │
    ├──────────────────────────────────────>│
    │   POST /bank/refund                  │
    │   {transaction_id, reason}           │
    │                                      ├─ Verify transaction exists
    │                                      ├─ Check status is SUCCESS
    │                                      ├─ Create refund record
    │                                      ├─ Update transaction to REFUNDED
    │   <──────────────────────────────────┤
    │   {success: true, refund: {...}}     │
    │                                      │
    ├─ Update dispute status              │
    ├─ Notify customer                    │
    │                                      │
```

### Failed Refund Flow
```
Python Agent                          Mock Bank API
    │                                      │
    ├──────────────────────────────────────>│
    │   GET /bank/transaction/TXN002       │
    │                                      ├─ Lookup transaction in DB
    │   <──────────────────────────────────┤
    │   {status: FAILED}                   │
    │                                      │
    ├─ Detect FAILED status               │
    │ (Cannot refund FAILED transactions)  │
    │                                      │
    ├─ Reject refund request              │
    ├─ Log reason                         │
    └─ Notify customer                    
```

## Configuration for Python Agent

### Environment Variables (recommended)
```bash
# In your Python environment
export BANK_API_URL=http://localhost:8080
export BANK_API_KEY=upi-dispute-resolver-secret-key-2024
export BANK_API_TIMEOUT=5
```

### Configuration File (config.yaml)
```yaml
bank_api:
  base_url: http://localhost:8080
  api_key: upi-dispute-resolver-secret-key-2024
  timeout: 5
  retry_attempts: 3
  retry_backoff: exponential

dispute_resolution:
  auto_approve_threshold: 0.8
  max_refund_amount: 100000.00
  require_merchant_verification: true
```

## Testing the Integration

### 1. Start Mock Bank API
```bash
cd mock-bank-api
mvn spring-boot:run
```

### 2. Run Python Integration Tests
```python
# test_integration.py
import requests
import json

def test_integration():
    """Test basic integration"""
    
    # Create test transaction
    print("Creating test transaction...")
    result = create_transaction("TXN_TEST_001", 1000.00, "SUCCESS")
    print(f"Result: {result}")
    
    # Fetch transaction
    print("\nFetching transaction...")
    result = get_transaction_details("TXN_TEST_001")
    print(f"Transaction: {result['transaction']}")
    
    # Process refund
    print("\nProcessing refund...")
    result = process_refund("TXN_TEST_001", "Integration test refund")
    print(f"Refund: {result['refund']}")
    
    print("\n✅ Integration test completed successfully")

if __name__ == "__main__":
    test_integration()
```

## API Response Mappings

### Transaction Status Mapping
```
Mock Bank Status    →  Python Agent Interpretation
SUCCESS            →  Transaction completed, eligible for refund
FAILED             →  Transaction failed, cannot be refunded
REFUNDED           →  Already refunded, cannot be refunded again
```

### Refund Status Mapping
```
Mock Bank Status    →  Python Agent Interpretation
SUCCESS            →  Refund processed successfully
PENDING            →  Refund awaiting processing
FAILED             →  Refund processing failed
```

## Performance Considerations

1. **Timeout Configuration**: Set timeout to 5-10 seconds for API calls
2. **Rate Limiting**: Implement rate limiting in Python agent
3. **Connection Pooling**: Reuse HTTP connections for efficiency
4. **Caching**: Cache transaction details if needed within session
5. **Batch Processing**: Process multiple disputes efficiently

## Security Considerations

1. **API Key Management**
   - Store API key in environment variables
   - Never hardcode in source code
   - Rotate keys regularly

2. **HTTPS in Production**
   - Use HTTPS for all API calls
   - Verify SSL certificates

3. **Input Validation**
   - Validate transaction IDs
   - Validate amounts
   - Sanitize reason text

4. **Error Handling**
   - Catch and log all exceptions
   - Never expose sensitive data in errors
   - Implement proper retry logic

## Monitoring and Logging

### Python Agent Should Log
```python
logger.info(f"Fetching transaction: {transaction_id}")
logger.info(f"Transaction status: {transaction['status']}")
logger.info(f"Processing refund for transaction: {transaction_id}")
logger.info(f"Refund successful: Refund ID {refund['refund_id']}")
logger.error(f"Refund failed: {error_message}")
```

### Mock Bank API Logs
```
INFO: Creating transaction with ID: TXN20240101001
INFO: Fetching transaction with ID: TXN20240101001
INFO: Processing refund for transaction ID: TXN20240101001
INFO: Refund record created with ID: 1
INFO: Transaction status updated to REFUNDED
WARN: Transaction not found with ID: TXN99999999999
WARN: Refund request: Transaction not found with ID: TXN99999999999
WARN: Refund not allowed for FAILED transaction: TXN20240101003
```

## Troubleshooting Integration

| Issue | Cause | Solution |
|-------|-------|----------|
| Connection refused | API not running | Start Mock Bank API server |
| 401 Unauthorized | Invalid API key | Verify API key in config |
| 404 Not Found | Transaction doesn't exist | Create transaction first |
| 400 Bad Request | FAILED transaction refund attempt | Only refund SUCCESS transactions |
| Timeout | Slow network/server | Increase timeout or check server |

## Summary

The Mock Bank API provides a complete REST interface for the Python Dispute Resolution Agent to:
1. Verify transaction details
2. Check transaction eligibility
3. Process refunds automatically
4. Maintain refund history

This integration enables automated dispute resolution with proper verification and audit trails.
