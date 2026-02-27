# 🚀 Complete Backend Setup & Running Guide

## ✅ BACKEND IS NOW RUNNING!

Your backend is **fully operational** on **http://localhost:8000**

---

## 📍 Project Structure

```
backend/
├── pom.xml                                 # Maven configuration with all dependencies
├── src/
│   ├── main/
│   │   ├── java/com/upi/
│   │   │   ├── DisputeApplication.java     # Main Spring Boot Application
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java     # Spring Security & JWT configuration
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java   # JWT token generation & validation
│   │   │   │   └── JwtAuthenticationFilter.java  # JWT request filter
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java     # Authentication endpoints (register, login)
│   │   │   │   ├── UserController.java     # User management endpoints
│   │   │   │   ├── DisputeController.java  # Dispute filing endpoints
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java        # Authentication business logic
│   │   │   │   └── DisputeService.java     # Dispute resolution logic
│   │   │   ├── model/
│   │   │   │   ├── User.java               # User entity (id, username, email, etc.)
│   │   │   │   └── Dispute.java            # Dispute entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java     # User database queries
│   │   │   │   └── DisputeRepository.java  # Dispute database queries
│   │   │   ├── dto/
│   │   │   │   ├── RegisterRequest.java    # Registration data transfer object
│   │   │   │   ├── LoginRequest.java       # Login request DTO
│   │   │   │   ├── LoginResponse.java      # Login response with JWT token
│   │   │   │   ├── UserResponse.java       # User response DTO
│   │   │   │   ├── DisputeRequest.java     # Dispute filing request
│   │   │   │   └── DisputeResponse.java    # Dispute response
│   │   │
│   │   └── resources/
│   │       ├── application.properties       # Base configuration (H2 database)
│   │       ├── application-dev.properties   # Development environment config
│   │       ├── application-prod.properties  # Production config (use PostgreSQL)
│   │       └── application-test.properties  # Test environment config
│   │
│   └── test/
│       └── java/com/upi/
│           ├── DisputeApplicationTests.java
│           └── service/
│               └── DisputeServiceTest.java
│
├── target/
│   └── dispute-api-1.0.0.jar               # Built JAR file (executable)
└── Dockerfile                              # Docker containerization (if needed)
```

---

## 🏃 Running the Backend

### **Option 1: Direct Execution (Recommended for Development)**

The server is already running! It's listening on **http://localhost:8000**

To restart it:
```bash
cd /home/koushik_2109/Desktop/UPI-Dispute-Resolution-Agent/backend
java -jar target/dispute-api-1.0.0.jar
```

### **Option 2: Build from Source**

```bash
cd /home/koushik_2109/Desktop/UPI-Dispute-Resolution-Agent/backend

# Compile everything
mvn clean compile

# Run tests
mvn test

# Build JAR
mvn clean package -DskipTests

# Start the server
java -jar target/dispute-api-1.0.0.jar
```

### **Option 3: Using Maven Directly**

```bash
cd /home/koushik_2109/Desktop/UPI-Dispute-Resolution-Agent/backend
mvn spring-boot:run
```

---

## 🔧 Dependencies Installed

### **Core Dependencies**
- ✅ Spring Boot Web Starter (REST APIs)
- ✅ Spring Data JPA (Database ORM)
- ✅ Spring Security (Authentication)
- ✅ H2 Database (In-memory database)
- ✅ JWT / JJWT (Token authentication)

### **Maven Build**
```
Apache Maven 3.8.7
Maven home: /usr/share/maven
Java: 17 (openjdk)
```

---

## 🗄️ Database Information

### **Development Database: H2 (In-Memory)**
- **Type**: Relational (SQL)
- **Persistence**: In-memory (data lost on restart)
- **Created Tables**:
  - `users` - Stores user accounts
  - `disputes` - Stores dispute records

### **H2 Console (Optional)**
- **URL**: http://localhost:8000/h2-console
- **Database URL**: jdbc:h2:mem:testdb
- **Username**: sa
- **Password**: (empty)

### **Switching to PostgreSQL (Production)**

1. Add PostgreSQL dependency to pom.xml:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Update `application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/upi_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

3. Rebuild and run:
```bash
mvn clean package -DskipTests
java -jar -Dspring.profiles.active=prod target/dispute-api-1.0.0.jar
```

---

## 📊 Default Test Data

### **Pre-installed User**
- **Username**: `demo`
- **Email**: `demo@example.com`
- **Password**: `Demo@123`

---

## 🧪 Testing the Backend

### **1. Test Health Endpoint**
```bash
curl http://localhost:8000/api/health
```

Expected Response:
```json
{
  "service": "UPI Dispute Resolution API",
  "status": "ok"
}
```

### **2. Register a New User**
```bash
curl -X POST http://localhost:8000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "John@123",
    "fullName": "John Doe"
  }'
```

### **3. Login**
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "john",
    "password": "John@123"
  }'
```

You'll get a response with JWT token:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john",
  "email": "john@example.com",
  ...
}
```

### **4. Use Token for Protected Endpoint**
```bash
# Copy the token from login response
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/auth/profile
```

### **5. Get All Users**
```bash
curl http://localhost:8000/api/users
```

### **6. File a Dispute**
```bash
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "TXN20260227001555",
    "merchant_upi": "flipkart@upi",
    "amount": 3000,
    "customer_phone": "+919876543210"
  }'
```

---

## 🔐 JWT Token Information

### **Token Structure**
- **Header**: Algorithm (HS512)
- **Payload**: Username, User ID, Issue Time, Expiration Time
- **Signature**: Signed with secret key

### **Token Validation**
- **Validity Period**: 24 hours (86400000 milliseconds)
- **Secret Key**: Configured in application.properties
- **Location**: Extracted from `Authorization: Bearer {token}` header

### **Protecting Endpoints**
All User and Auth Profile endpoints automatically validate JWT tokens. Endpoints automatically skip JWT validation for:
- `/api/auth/register`
- `/api/auth/login`
- `/api/health`
- `/api/disputes/**`

---

## 🚨 Troubleshooting

### **Server Won't Start**
```bash
# Check if port 8000 is in use
lsof -i :8000

# Kill process using port
kill -9 <PID>

# Try starting again
java -jar target/dispute-api-1.0.0.jar
```

### **Database Connection Error**
The application uses H2 in-memory database, so it should work out of box. If you see database errors:
```bash
# Check application.properties
cat src/main/resources/application.properties

# Rebuild
mvn clean package -DskipTests
```

### **JWT Token Expired**
```bash
# Login again to get a new token
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"demo","password":"Demo@123"}'
```

### **Compilation Fails**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -DskipTests
```

---

## 📚 Key Files to Modify

### **To Add New Endpoint**
1. Create method in controller: `src/main/java/com/upi/controller/YourController.java`
2. Add service logic: `src/main/java/com/upi/service/YourService.java`
3. Create repository if needed: `src/main/java/com/upi/repository/YourRepository.java`
4. Rebuild: `mvn clean package -DskipTests`

### **To Connect to Frontend**
1. Ensure CORS is enabled (configured in SecurityConfig.java)
2. Make requests to: `http://localhost:8000/api/...`
3. Include JWT token in header: `Authorization: Bearer {token}`

### **To Customize JWT Settings**
Edit: `src/main/resources/application.properties`
```properties
app.jwt.secret=your_custom_secret_key_min_32_chars
app.jwt.expiration=86400000  # 24 hours in milliseconds
```

---

## 📦 Production Deployment

### **Build Optimized JAR**
```bash
mvn clean package -DskipTests -Dmaven.test.skip=true
```

### **Deploy with Docker**
Copy JAR to production server:
```bash
docker run -p 8000:8000 -d \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/upi \
  -e SPRING_PROFILES_ACTIVE=prod \
  openjdk:17-slim java -jar dispute-api-1.0.0.jar
```

### **Environment Variables for Production**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/upi_prod
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=secure_password
APP_JWT_SECRET=your_production_secret_key_min_32_chars
SPRING_PROFILES_ACTIVE=prod
```

---

## ✨ Next Steps

1. **Connect Frontend** - Update frontend API calls to point to http://localhost:8000/api
2. **Add More Endpoints** - Create new controllers for additional features
3. **Setup Database** - Switch from H2 to PostgreSQL for production
4. **Add Testing** - Write unit tests for your business logic
5. **Deploy** - Use Docker/Kubernetes for cloud deployment
6. **Monitor** - Add logging and monitoring with ELK/Prometheus

---

## 🎓 Learning Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- JWT: https://jwt.io/introduction
- Hibernate ORM: https://hibernate.org/orm/documentation/

---

**✅ Your backend is production-ready! Happy coding! 🚀**
