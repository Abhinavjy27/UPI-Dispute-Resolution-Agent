# Phone-Based Authentication Setup

## Frontend Implementation ✅

The frontend now supports simple phone-based login without OTP.

### Flow:
1. User enters 10-digit phone number
2. System checks localStorage for existing session
3. If not logged in, shows login screen
4. After login, user can:
   - File disputes (phone auto-filled)
   - View their dashboard with all disputes
   - Logout anytime

### Components Created:
- `AuthContext.tsx` - Phone auth state management
- `PhoneLogin.tsx` - Login screen with phone input
- `UserDashboard.tsx` - Shows user's dispute history
- Updated `App.tsx` - Routes for login/dashboard
- Updated `DisputeForm.tsx` - Auto-fills phone if logged in

---

## Backend Requirements 🔧

Your backend needs these endpoints:

### 1. Login/Signup (Optional)
```
POST /api/auth/phone
Body: { "phone": "+919876543210" }
Response: { "userId": "user123", "phone": "+919876543210", "disputes": [] }
```

**Note:** For hackathon, you can skip this endpoint. Frontend stores phone in localStorage.

### 2. Get User's Disputes
```
GET /api/disputes/user/:phone
Response: {
  "disputes": [
    {
      "disputeId": "DIS1234567890",
      "transactionId": "TXN20260227000001",
      "merchantUpi": "amazon@upi",
      "amount": 5000,
      "status": "APPROVED",
      "riskScore": 0.15,
      "createdAt": "2026-02-25T10:30:00Z"
    }
  ]
}
```

### 3. Link Dispute to User
When creating dispute, use `customerPhone` to link it:

```python
# In your create dispute endpoint
@app.post("/api/disputes")
def create_dispute(data: DisputeRequest):
    dispute = {
        "disputeId": generate_id(),
        "transactionId": data.transactionId,
        "customerPhone": data.customerPhone,  # Link to user
        "status": "PENDING",
        # ... other fields
    }
    # Store in database
    save_dispute(dispute)
    return dispute
```

---

## Database Schema

Add `customerPhone` field to disputes table:

```sql
disputes:
  - disputeId (PK)
  - transactionId
  - merchantUpi
  - amount
  - customerPhone  ← Index this!
  - status
  - createdAt
```

---

## Security Notes

⚠️ **This is NOT production-ready** - it's for hackathon demo:
- No password/OTP verification
- Anyone with phone number can access
- Phone stored in localStorage (can be cleared)

For production, add:
- OTP verification via Twilio/Supabase
- JWT tokens
- Secret key encryption
- Session management

---

## How to Test

1. Frontend already working at http://localhost:5174
2. Click "File Dispute Now"
3. Enter phone number in login screen
4. Form auto-fills your phone
5. Submit dispute
6. Go to Dashboard to see all your disputes

Enjoy! 🚀
