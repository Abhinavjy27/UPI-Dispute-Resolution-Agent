# Complete Backend API Documentation

## 🎯 Backend Status: ✅ RUNNING ON PORT 8000

### Server Information
- **Host**: http://localhost:8000
- **Database**: H2 (In-Memory)
- **Framework**: Spring Boot 3.1.5
- **Java**: Java 17
- **Authentication**: JWT Token-based

---

## 📋 Available Endpoints

### 1. **AUTH ENDPOINTS** - User Authentication & Registration

#### **Register New User**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Full Name",
  "phone": "+919876543210",
  "address": "Address"
}

Response: 201 CREATED
{
  "id": 1,
  "username": "newuser",
  "email": "user@example.com",
  "fullName": "Full Name",
  "phone": "+919876543210",
  "address": "Address",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-02-27T23:07:56"
}
```

#### **Login User**
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "newuser",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "newuser",
  "email": "user@example.com",
  "fullName": "Full Name",
  "userId": 1,
  "isVerified": false,
  "createdAt": "2026-02-27T23:07:56"
}
```

#### **Get User Profile** (Protected - requires JWT)
```http
GET /api/auth/profile
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "username": "newuser",
  "email": "user@example.com",
  "fullName": "Full Name",
  ...
}
```

#### **Update User Profile** (Protected - requires JWT)
```http
PUT /api/auth/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Updated Name",
  "phone": "+919876543210",
  "address": "New Address"
}

Response: 200 OK
```

#### **Logout**
```http
POST /api/auth/logout

Response: 200 OK
{
  "message": "Logged out successfully"
}
```

---

### 2. **USER MANAGEMENT ENDPOINTS**

#### **Get User by ID**
```http
GET /api/users/{userId}

Response: 200 OK
{
  "id": 1,
  "username": "newuser",
  "email": "user@example.com",
  ...
}
```

#### **Get User by Username**
```http
GET /api/users/by-username/{username}

Response: 200 OK
```

#### **Get All Users**
```http
GET /api/users

Response: 200 OK
[
  {
    "id": 1,
    "username": "user1",
    ...
  },
  {
    "id": 2,
    "username": "user2",
    ...
  }
]
```

#### **Delete User**
```http
DELETE /api/users/{userId}

Response: 200 OK
{
  "message": "User deleted successfully"
}
```

---

### 3. **DISPUTE RESOLUTION ENDPOINTS**

#### **File New Dispute**
```http
POST /api/disputes
Content-Type: application/json

{
  "transaction_id": "TXN20260227123456",
  "merchant_upi": "amazon@upi",
  "amount": 5000,
  "customer_phone": "+919876543210"
}

Response: 201 CREATED
{
  "success": true,
  "dispute_id": "DIS_000001",
  "status": "PENDING",
  "amount": 5000,
  "neft_reference": "NEFT20260227123456",
  "message": "Dispute filed successfully"
}
```

#### **Get Dispute Status**
```http
GET /api/disputes/{dispute_id}

Response: 200 OK
{
  "dispute_id": "DIS_000001",
  "transaction_id": "TXN20260227123456",
  "merchant_upi": "amazon@upi",
  "amount": 5000,
  "status": "PENDING",
  ...
}
```

---

### 4. **HEALTH CHECK**

#### **Server Health Status**
```http
GET /api/health

Response: 200 OK
{
  "service": "UPI Dispute Resolution API",
  "status": "ok"
}
```

---

## 🔐 Authentication Example

### Step 1: Register
```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@123",
    "fullName": "Test User"
  }'
```

### Step 2: Login
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "Test@123"
  }'
```

### Step 3: Use Token for Protected Endpoints
```bash
curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  http://localhost:8000/api/auth/profile
```

---

## 📦 Features Implemented

### ✅ **Authentication & Security**
- User registration with email/password
- User login with JWT token generation
- Password hashing with BCrypt
- JWT token validation
- Protected endpoints with Bearer token auth

### ✅ **User Management**
- Create new users
- View user profiles
- Update user information
- Delete users
- List all users

### ✅ **Dispute Resolution**
- File new disputes
- Get dispute status
- Dispute tracking with IDs
- NEFT reference generation

### ✅ **Database**
- H2 In-Memory Database
- Automatic schema creation
- User entity with validation
- Dispute entity with relationships

### ✅ **Security Features**
- CORS enabled for frontend integration
- CSRF protection
- Password validation
- Email validation
- JWT token expiration (24 hours)

---

## 🛠️ Technical Stack

| Component | Technology |
|-----------|-----------|
| Backend Framework | Spring Boot 3.1.5 |
| Language | Java 17 |
| Database | H2 (In-Memory) |
| Authentication | JWT (JSON Web Tokens) |
| Security | Spring Security + BCrypt |
| Build Tool | Maven 3.8.7 |
| Server | Embedded Tomcat |
| ORM | Hibernate (JPA) |

---

## 📄 Sample Data

### Test User (Pre-created)
- **Username**: demo
- **Email**: demo@example.com
- **Password**: Demo@123
- **Full Name**: Demo User

---

## 🚀 Quick Start Commands

### Test Health Status
```bash
curl http://localhost:8000/api/health
```

### Register New User
```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "youruser",
    "email": "your@email.com",
    "password": "SecurePass123",
    "fullName": "Your Name"
  }'
```

### Login and Get Token
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "youruser",
    "password": "SecurePass123"
  }'
```

### Get All Users
```bash
curl http://localhost:8000/api/users
```

### File a Dispute
```bash
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "TXN12345678",
    "merchant_upi": "merchant@upi",
    "amount": 1000,
    "customer_phone": "+919876543210"
  }'
```

---

## ✨ What You Can Build Next

1. **Frontend Dashboard** - React/Vue UI for user management
2. **Email Notifications** - Send JWT verification links
3. **Advanced Search** - Filter/search disputes by date/amount
4. **Admin Panel** - Manage users and disputes
5. **Analytics** - Dispute success rates and metrics
6. **Mobile API** - Same endpoints for mobile apps
7. **Payment Integration** - Razorpay/Stripe webhook handling
8. **Reporting** - Export dispute data to PDF/Excel

---

## 📞 Support

All endpoints return proper HTTP status codes:
- **200 OK** - Success
- **201 CREATED** - Resource created
- **400 BAD REQUEST** - Invalid input
- **401 UNAUTHORIZED** - Authentication required
- **404 NOT FOUND** - Resource not found
- **500 INTERNAL SERVER ERROR** - Server error

---

**🎉 Your backend is fully functional and ready for integration!**
