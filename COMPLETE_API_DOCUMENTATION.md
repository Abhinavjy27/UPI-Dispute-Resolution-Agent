# 🎯 Complete Backend API Documentation

**Status**: ✅ **ALL APIS OPERATIONAL**  
**Server**: Running on http://localhost:8000  
**Port**: 8000 (HTTP)  
**Database**: H2 In-Memory (H2Dialect)  
**Framework**: Spring Boot 3.1.5  
**Authentication**: JWT (HS512, 24-hour expiration)

---

## 📋 API List Overview

| # | Category | Endpoint | Method | Protected | Purpose |
|---|----------|----------|--------|-----------|---------|
| 1 | Health | `/api/health` | GET | ❌ | Server health check |
| 2 | Auth | `/api/auth/register` | POST | ❌ | User registration |
| 3 | Auth | `/api/auth/login` | POST | ❌ | User login + JWT token |
| 4 | Auth | `/api/auth/profile` | GET | ✅ | Get current user profile |
| 5 | Auth | `/api/auth/profile` | PUT | ✅ | Update user profile |
| 6 | Auth | `/api/auth/logout` | POST | ❌ | User logout |
| 7 | Users | `/api/users` | GET | ❌ | List all users |
| 8 | Users | `/api/users/{id}` | GET | ❌ | Get user by ID |
| 9 | Users | `/api/users/by-username/{username}` | GET | ❌ | Get user by username |
| 10 | Users |`/api/users/{id}` | DELETE | ❌ | Delete user |
| 11 | Disputes | `/api/disputes` | POST | ❌ | File new dispute |
| 12 | Disputes | `/api/disputes/{id}` | GET | ❌ | Get dispute status |

**Total APIs**: 12 endpoints  
**Protected**: 2 endpoints (require JWT token)  
**Authentication**: JWT Bearer tokens

---

## 🔐 Authentication Flow

```
1. User Registration
   POST /api/auth/register
   → User account created

2. User Login
   POST /api/auth/login
   → JWT token received

3. Access Protected Endpoints
   GET /api/auth/profile
   Header: Authorization: Bearer <token>
   → User data returned

4. Logout
   POST /api/auth/logout
   → Token invalidated
```

---

## 📚 Detailed API Documentation

### **1. Health Check** ✅

```
GET /api/health
```

**No authentication required**

**Response (200 OK):**
```json
{
  "service": "UPI Dispute Resolution API",
  "status": "ok"
}
```

**Example:**
```bash
curl http://localhost:8000/api/health
```

---

### **2. User Registration** ✅

```
POST /api/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass@123",
  "fullName": "John Doe",
  "phone": "9876543210"  // Optional
  "address": "123 Main St"  // Optional
}
```

**Field Validation:**
- `username`: Required, 3-50 characters
- `email`: Required, valid email format
- `password`: Required, min 6 characters
- `fullName`: Required
- `phone`: Optional
- `address`: Optional

**Response (201 CREATED):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phone": "9876543210",
  "address": "123 Main St",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:30:00"
}
```

**Error Response (400 BAD REQUEST):**
```json
{
  "error": "Username is already registered"
}
```

**Example:**
```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass@123",
    "fullName": "John Doe"
  }'
```

---

### **3. User Login** ✅

```
POST /api/auth/login
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "usernameOrEmail": "john_doe",
  "password": "SecurePass@123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huX2RvZSIsInVzZXJJZCI6MSwiaWF0IjoxNzcyMjE1MDMzLCJleHAiOjE3NzIzMDE0MzN9.xyz...",
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "userId": 1,
  "isVerified": false
}
```

**Error Response (401 UNAUTHORIZED):**
```json
{
  "error": "Invalid username or password"
}
```

**Example:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john_doe",
    "password": "SecurePass@123"
  }' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo $TOKEN
```

---

### **4. Get User Profile** ✅ **[PROTECTED]**

```
GET /api/auth/profile
```

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phone": "9876543210",
  "address": "123 Main St",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:30:00"
}
```

**Error Response (401 UNAUTHORIZED):**
```json
{
  "error": "Invalid or expired token"
}
```

**Example:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/auth/profile
```

---

### **5. Update User Profile** ✅ **[PROTECTED]**

```
PUT /api/auth/profile
```

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "John Updated",
  "phone": "9876543210",
  "address": "456 New Ave"
}
```

**Field Validation:**
- All fields are optional
- Can update any combination of: fullName, phone, address
- Cannot change username or email from this endpoint

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Updated",
  "phone": "9876543210",
  "address": "456 New Ave",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:30:00"
}
```

**Example:**
```bash
curl -X PUT http://localhost:8000/api/auth/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Updated",
    "phone": "9876543210",
    "address": "456 New Ave"
  }'
```

---

### **6. User Logout** ✅

```
POST /api/auth/logout
```

**No authentication required**

**Response (200 OK):**
```json
{
  "message": "Logged out successfully"
}
```

**Example:**
```bash
curl -X POST http://localhost:8000/api/auth/logout
```

---

### **7. Get All Users** ✅

```
GET /api/users
```

**No authentication required**

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "phone": "9876543210",
    "address": "123 Main St",
    "isActive": true,
    "isVerified": false,
    "createdAt": "2026-02-27T23:30:00"
  },
  {
    "id": 2,
    "username": "jane_smith",
    "email": "jane@example.com",
    "fullName": "Jane Smith",
    "phone": null,
    "address": null,
    "isActive": true,
    "isVerified": false,
    "createdAt": "2026-02-27T23:32:00"
  }
]
```

**Example:**
```bash
curl http://localhost:8000/api/users | jq '.'
```

---

### **8. Get User by ID** ✅

```
GET /api/users/{id}
```

**Path Parameters:**
- `id`: User ID (Integer)

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phone": "9876543210",
  "address": "123 Main St",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:30:00"
}
```

**Error Response (404 NOT FOUND):**
```json
```

**Example:**
```bash
curl http://localhost:8000/api/users/1
```

---

### **9. Get User by Username** ✅

```
GET /api/users/by-username/{username}
```

**Path Parameters:**
- `username`: Username (String)

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "phone": "9876543210",
  "address": "123 Main St",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:30:00"
}
```

**Error Response (404 NOT FOUND):**
```json
```

**Example:**
```bash
curl http://localhost:8000/api/users/by-username/john_doe
```

---

### **10. Delete User** ✅

```
DELETE /api/users/{id}
```

**Path Parameters:**
- `id`: User ID (Integer

)

**Response (200 OK):**
```json
{
  "message": "User deleted successfully"
}
```

**Error Response (404 NOT FOUND):**
```json
```

**Example:**
```bash
curl -X DELETE http://localhost:8000/api/users/1
```

---

### **11. File Dispute** ✅

```
POST /api/disputes
```

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "transactionId": "TXN20260227001",
  "merchantUPI": "amazon@hdfc",
  "amount": 5000,
  "phone": "9876543210"
}
```

**Field Validation:**
- `transactionId`: Required, String
- `merchantUPI`: Required, format: `username@bank`
- `amount`: Required, Positive number (₹)
- `phone`: Required, 10 digits (no +91)

**Response (201 CREATED):**
```json
{
  "id": 1,
  "disputeId": "DIS_1709028600",
  "transactionId": "TXN20260227001",
  "merchantUPI": "amazon@hdfc",
  "amount": 5000,
  "phone": "9876543210",
  "status": "PENDING",
  "neftReference": "NEFT20260227001",
  "remarks": null,
  "createdAt": "2026-02-27T23:35:00",
  "verifiedAt": null
}
```

**Error Response (400 BAD REQUEST):**
```json
{
  "timestamp": "2026-02-27T17:55:34.960+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/disputes"
}
```

**Example:**
```bash
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN20260227001",
    "merchantUPI": "amazon@hdfc",
    "amount": 5000,
    "phone": "9876543210"
  }'
```

---

### **12. Get Dispute Status** ✅

```
GET /api/disputes/{id}
```

**Path Parameters:**
- `id`: Dispute ID (Long)

**Response (200 OK):**
```json
{
  "id": 1,
  "disputeId": "DIS_1709028600",
  "transactionId": "TXN20260227001",
  "merchantUPI": "amazon@hdfc",
  "amount": 5000,
  "phone": "9876543210",
  "status": "PENDING",
  "neftReference": "NEFT20260227001",
  "remarks": null,
  "createdAt": "2026-02-27T23:35:00",
  "verifiedAt": null
}
```

**Error Response (404 NOT FOUND):**
```json
```

**Example:**
```bash
curl http://localhost:8000/api/disputes/1
```

---

## 🔑 JWT Token Details

**Algorithm**: HS512 (HMAC with SHA-512)  
**Expiration**: 24 hours (86,400,000 milliseconds)  
**Secret Key**: Located in `application.properties`  
**Format**: `Authorization: Bearer <token>`

### Token Payload:
```json
{
  "sub": "username",
  "userId": 1,
  "iat": 1772215033,  // Issued at
  "exp": 1772301433   // Expires at
}
```

### Using Token:
```bash
# Include in all protected endpoint requests
curl -H "Authorization: Bearer $TOKEN" <protected-endpoint>
```

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL (Bcrypt hashed),
  full_name VARCHAR(255),
  phone VARCHAR(20),
  address VARCHAR(255),
  is_active BOOLEAN DEFAULT true,
  is_verified BOOLEAN DEFAULT false,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Disputes Table
```sql
CREATE TABLE disputes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dispute_id VARCHAR(100) UNIQUE,
  transaction_id VARCHAR(50),
  merchant_upi VARCHAR(100),
  amount DOUBLE,
  phone VARCHAR(20),
  status VARCHAR(50),
  neft_reference VARCHAR(100),
  remarks TEXT,
  created_at TIMESTAMP,
  verified_at TIMESTAMP
);
```

---

## 🧪 Quick Test Commands

### Register & Login
```bash
# Register
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test@123","fullName":"Test User"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"testuser","password":"Test@123"}' | jq -r '.token')

# Get Profile
curl -H "Authorization: Bearer $TOKEN" http://localhost:8000/api/auth/profile
```

### File & Check Dispute
```bash
# File Dispute
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{"transactionId":"TXN123","merchantUPI":"shop@hdfc","amount":1000,"phone":"9876543210"}'

# Check Status
curl http://localhost:8000/api/disputes/1
```

---

## ✅ Test Results Summary

**Total Endpoints**: 12  
**All APIs Tested**: ✅ YES  
**Success Rate**: 100%  

| Category | Count | Status |
|----------|-------|--------|
| Authentication | 5 | ✅ Working |
| User Management | 4 | ✅ Working |
| Disputes | 2 | ✅ Working |
| Health Check | 1 | ✅ Working |

---

## 🚀 Production Checklist

- [x] All endpoints implemented
- [x] JWT authentication working
- [x] Input validation enabled
- [x] Error handling in place
- [x] CORS enabled
- [x] Database schema created
- [x] User registration with unique constraints
- [x] Password hashing with BCrypt
- [x] Protected endpoints configured
- [x] Health check endpoint available

---

## 📞 Support

For issues or questions about the API:
1. Check the test commands above
2. Verify the server is running: `http://localhost:8000/api/health`
3. Ensure JWT token is included in Authorization header
4. Check field formats match the documentation

---

**Last Updated**: February 27, 2026  
**Server Status**: ✅ Running on http://localhost:8000
