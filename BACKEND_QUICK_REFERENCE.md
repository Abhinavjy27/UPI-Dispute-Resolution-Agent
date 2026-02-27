# 🎯 Backend API Quick Reference

## ✅ Server Status: RUNNING on http://localhost:8000

---

## 🚀 5-Minute Quick Start

### 1️⃣ **Test Server Health**
```bash
curl http://localhost:8000/api/health
```
✅ Should return: `{"service":"UPI Dispute Resolution API","status":"ok"}`

### 2️⃣ **Register User**
```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "TestPass@123",
    "fullName": "Test User"
  }'
```
✅ Get `id`, `username`, `email` in response

### 3️⃣ **Login User**
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "TestPass@123"
  }'
```
✅ Get JWT `token` in response (save this!)

### 4️⃣ **Use Token to Access Protected Endpoint**
```bash
# Replace TOKEN with actual token from login
TOKEN="your_token_here"

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/auth/profile
```

### 5️⃣ **Get All Users**
```bash
curl http://localhost:8000/api/users
```
✅ Returns list of all registered users

---

## 📝 Complete Endpoint Reference

### **AUTHENTICATION ENDPOINTS** 
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/auth/register` | ❌ | Register new user |
| POST | `/api/auth/login` | ❌ | Login & get JWT token |
| GET | `/api/auth/profile` | ✅ | Get current user profile |
| PUT | `/api/auth/profile` | ✅ | Update user profile |
| POST | `/api/auth/logout` | ❌ | Logout (clear token) |

### **USER MANAGEMENT ENDPOINTS**
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/users` | ❌ | Get all users |
| GET | `/api/users/{id}` | ❌ | Get user by ID |
| GET | `/api/users/by-username/{username}` | ❌ | Get user by username |
| DELETE | `/api/users/{id}` | ❌ | Delete user |

### **DISPUTE ENDPOINTS**
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/api/disputes` | ❌ | File new dispute |
| GET | `/api/disputes/{id}` | ❌ | Get dispute details |

### **HEALTH CHECK**
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| GET | `/api/health` | ❌ | Check server health |

(✅ = Requires JWT Token, ❌ = Public Access)

---

## 🔑 Pre-Registered Test User

```
Username: demo
Email: demo@example.com
Password: Demo@123
```

Quick login:
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"demo","password":"Demo@123"}'
```

---

## 📋 Request/Response Examples

### **Example 1: Register → Login → Get Profile Flow**

#### REGISTER
```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "Alice@2026",
    "fullName": "Alice Wonder"
  }'
```

**Response:**
```json
{
  "id": 5,
  "username": "alice",
  "email": "alice@example.com",
  "fullName": "Alice Wonder",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:07:56"
}
```

#### LOGIN
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "alice",
    "password": "Alice@2026"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGljZSIsInVzZXJJZCI6NSwiaWF0IjoxNjcyMDEyMDc2LCJleHAiOjE2NzIwOTg0NzZ9.abc123...",
  "username": "alice",
  "email": "alice@example.com",
  "fullName": "Alice Wonder",
  "userId": 5,
  "isVerified": false
}
```

#### GET PROFILE (Using Token)
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGljZSIsInVzZXJJZCI6NSwiaWF0IjoxNjcyMDEyMDc2LCJleHAiOjE2NzIwOTg0NzZ9.abc123..."

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/auth/profile
```

**Response:**
```json
{
  "id": 5,
  "username": "alice",
  "email": "alice@example.com",
  "fullName": "Alice Wonder",
  "phone": null,
  "address": null,
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:07:56"
}
```

---

### **Example 2: File A Dispute**

```bash
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "TXN20260227456789",
    "merchant_upi": "flipkart@upi",
    "amount": 2500,
    "customer_phone": "+919876543210"
  }'
```

**Response:**
```json
{
  "success": true,
  "dispute_id": "DIS_000002",
  "status": "PENDING",
  "amount": 2500,
  "neft_reference": "NEFT20260227456789",
  "message": "Dispute initiated",
  "created_at": "2026-02-27T23:10:00"
}
```

---

### **Example 3: Get Dispute Status**

```bash
curl http://localhost:8000/api/disputes/DIS_000002
```

**Response:**
```json
{
  "dispute_id": "DIS_000002",
  "transaction_id": "TXN20260227456789",
  "merchant_upi": "flipkart@upi",
  "customer_phone": "+919876543210",
  "amount": 2500,
  "status": "PENDING",
  "created_at": "2026-02-27T23:10:00"
}
```

---

## 🧪 Testing with Postman

### **Setup Postman Environment**

1. Create new collection: `UPI Backend`
2. Create environment variable: `token`
3. Set base URL: `{{base_url}}` = `http://localhost:8000`

### **Test Steps**

1. **Register Endpoint**
   - Method: `POST`
   - URL: `{{base_url}}/api/auth/register`
   - Body: Raw JSON with user data
   - Tests: `pm.environment.set("userId", pm.response.json().id);`

2. **Login Endpoint**
   - Method: `POST`
   - URL: `{{base_url}}/api/auth/login`
   - Body: Login credentials
   - Tests: `pm.environment.set("token", pm.response.json().token);`

3. **Get Profile Endpoint**
   - Method: `GET`
   - URL: `{{base_url}}/api/auth/profile`
   - Headers: `Authorization: Bearer {{token}}`

---

## ⚠️ Common Issues & Fixes

### **"Cannot load driver class: org.sqlite.JDBC"**
- **Fix**: Rebuild with Maven
  ```bash
  mvn clean package -DskipTests
  java -jar target/dispute-api-1.0.0.jar
  ```

### **Port 8000 already in use**
- **Fix**: Kill the process
  ```bash
  lsof -i :8000
  kill -9 <PID>
  ```

### **JWT Token expired**
- **Fix**: Login again to get a new token
  ```bash
  curl -X POST http://localhost:8000/api/auth/login ...
  ```

### **401 Unauthorized**
- **Fix**: Ensure token is in header
  ```bash
  curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8000/api/auth/profile
  ```

### **400 Bad Request**
- **Fix**: Check JSON format and required fields
  ```bash
  # Ensure Content-Type header is set
  -H "Content-Type: application/json"
  ```

---

## 🔄 Workflow Examples

### **Complete User Registration & Dispute Filing Flow**

```bash
#!/bin/bash

# Step 1: Register
REGISTER=$(curl -s -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "workflow_user",
    "email": "workflow@example.com",
    "password": "WorkFlow@123",
    "fullName": "Workflow Test"
  }')

USER_ID=$(echo $REGISTER | jq '.id')
echo "✅ User registered: $USER_ID"

# Step 2: Login
LOGIN=$(curl -s -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "workflow_user",
    "password": "WorkFlow@123"
  }')

TOKEN=$(echo $LOGIN | jq -r '.token')
echo "✅ Logged in, token: $TOKEN"

# Step 3: Get Profile
PROFILE=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/auth/profile)

echo "✅ Profile retrieved:"
echo $PROFILE | jq '.'

# Step 4: File Dispute
DISPUTE=$(curl -s -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "TXN'$USER_ID'",
    "merchant_upi": "amazon@upi",
    "amount": 5000,
    "customer_phone": "+919876543210"
  }')

echo "✅ Dispute filed:"
echo $DISPUTE | jq '.'
```

---

## 📊 API Response Codes

| Code | Meaning | When |
|------|---------|------|
| **200** | OK | Successful request |
| **201** | Created | Resource created |
| **400** | Bad Request | Invalid input |
| **401** | Unauthorized | Missing/invalid token |
| **404** | Not Found | Resource doesn't exist |
| **500** | Server Error | Internal error |

---

## 🎯 Best Practices

1. **Always include `Content-Type: application/json`** in POST/PUT requests
2. **Store JWT token securely** (localStorage/sessionStorage in frontend)
3. **Include token in Authorization header** as `Bearer {token}`
4. **Validate token before using** (check expiration)
5. **Use HTTPS in production** (for security)
6. **Never hardcode credentials** (use environment variables)

---

**🎉 You have a fully functional backend! Start integrating with your frontend! 🚀**
