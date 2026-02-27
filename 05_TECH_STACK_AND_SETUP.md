# 05_TECH_STACK_AND_SETUP

## 🛠️ Technology Stack

### **Why These Choices?**

```
Requirements for 12-hour hackathon:
├─ Fast setup (minutes, not hours)
├─ No complex DevOps (no Docker, no databases to manage)
├─ Production-grade (not toys, real companies use these)
├─ Easy to debug (good error messages, documentation)
└─ Scalable (can upgrade later without rewriting)

Our Choices:
├─ Frontend: React 18 + Vite + Tailwind CSS + Axios
├─ Backend: Python 3.11 + FastAPI + SQLAlchemy
├─ Database: SQLite (zero setup!)
└─ Deployment: Vercel + Railway/Render (if demo needed)
```

### **Frontend Stack**

| Technology | Version | Why |
|-----------|---------|-----|
| React | 18+ | Standard choice, tons of components available |
| Vite | Latest | 10x faster builds than CRA, instant reload |
| Axios | 1.6+ | Simple HTTP calls, good error handling |
| Tailwind CSS | 3+ | Fast UI styling, pre-built components |
| Recharts | 2+ | Beautiful charts for optional dashboard |

### **Backend Stack**

| Technology | Version | Why |
|-----------|---------|-----|
| Java | 17+ | Stable, widely used, great ecosystem |
| Spring Boot | 3.0+ | REST APIs, built-in validation, fast setup |
| Spring Data JPA | Latest | ORM (Hibernate), no raw SQL |
| Maven | 3.8+ | Build tool, dependency management |
| SQLite JDBC | Latest | Zero setup, works perfectly for hackathon |

### **Database**

| Technology | Why |
|-----------|-----|
| SQLite | Zero setup (single file), all features, perfect for hackathon |
| PostgreSQL | Later (production scale, redundancy, backups) |

### **Deployment (Optional)**

| Component | Service | Why |
|-----------|---------|-----|
| Frontend | Vercel | Free tier, instant deploys, great DX |
| Backend | Railway or Render | Free tier, Python support, easy setup |
| Code | GitHub | Version control, backup |

---

## ⚙️ Local Setup (15 minutes)

### **Prerequisites**

```bash
# Check you have these installed
java --version         # Should be 17+ (run: java -version)
mvn --version          # Should be 3.8+
node --version         # Should be 16+
npm --version          # Should be 8+

# If not installed:
# macOS: brew install java maven node
# Ubuntu: sudo apt install openjdk-17-jdk maven nodejs npm
# Windows: Download from oracle.com (Java), maven.apache.org, and nodejs.org
```

### **Step 1: Clone/Create Project**

```bash
# Create project directory
mkdir upi-dispute-resolution
cd upi-dispute-resolution

# Initialize source control (optional, but good practice)
git init

# Create folder structure
mkdir -p backend frontend
```

### **Step 2: Setup Backend**

```bash
cd backend

# Create Spring Boot project (or use existing Maven project)
# If creating new:
mvn archetype:generate -DgroupId=com.upi -DartifactId=dispute-api \
  -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

# Or if using existing pom.xml:
# Just run Maven to install dependencies:
mvn clean install

# Verify installation
mvn --version  # Should show Maven 3.8+
java -version  # Should show Java 17+

# Check dependencies downloaded
ls -la ~/.m2/repository/org/springframework/  # Should have Spring JARs
```

### **Step 3: Setup Frontend**

```bash
cd ../frontend

# Create React project with Vite
npm create vite@latest . -- --template react

# Install dependencies
npm install
npm install axios recharts

# Verify installation
npm list react axios
# Should show versions for both packages
```

### **Step 4: Verify Everything Works**

```bash
# Terminal 1: Start backend
cd backend
mvn spring-boot:run  # or mvn spring-boot:run -DskipTests

# You should see:
# INFO ... Application started in ... seconds
# Look for: "Tomcat started on port(s): 8000"
# Look for: "Started DemoApplication" ✓

# Terminal 2: Start frontend
cd frontend
npm run dev

# You should see:
# Local:   http://localhost:5173/
# Look for: VITE v X.X.X ready in XXX ms ✓
```

**If both start:** ✅ Setup successful!

---

## 📁 Project Structure

```
upi-dispute-resolution/
│
├── backend/                          # Spring Boot (Maven)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/upi/
│   │   │   │   ├── DemoApplication.java      # Main entry point
│   │   │   │   ├── model/
│   │   │   │   │   └── Dispute.java          # JPA entity
│   │   │   │   ├── controller/
│   │   │   │   │   └── DisputeController.java # REST endpoints
│   │   │   │   ├── repository/
│   │   │   │   │   └── DisputeRepository.java # JPA repository
│   │   │   │   ├── service/
│   │   │   │   │   ├── DisputeService.java    # Business logic
│   │   │   │   │   └── BankApiService.java    # Bank API calls
│   │   │   │   └── dto/
│   │   │   │       └── DisputeRequest.java    # Request DTOs
│   │   │   └── resources/
│   │   │       ├── application.properties     # Config
│   │   │       └── application-dev.properties # Dev config
│   │   └── test/                             # Unit tests
│   ├── target/                               # Compiled JARs (git ignore)
│   ├── pom.xml                               # Maven dependencies
│   └── disputes.db                           # SQLite database (auto-created)
│
├── frontend/                         # React + Vite
│   ├── node_modules/                 # Dependencies (git ignore)
│   ├── src/
│   │   ├── App.jsx                   # Main app component
│   │   ├── components/
│   │   │   ├── DisputeForm.jsx       # File dispute form
│   │   │   ├── StatusPage.jsx        # Track dispute
│   │   │   ├── Dashboard.jsx         # Optional: merchant dashboard
│   │   │   └── RiskScore.jsx         # Optional: risk assessment
│   │   ├── styles/
│   │   │   └── globals.css           # Tailwind imports
│   │   ├── main.jsx                  # React entry point
│   │   └── vite.svg
│   ├── public/
│   ├── .gitignore
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── tailwind.config.js
│
├── .gitignore                        # Ignore node_modules, venv, .env
├── .env.example                      # Example env vars (check in)
├── README.md
└── package-lock.json (frontend)
```

---

## 🚀 Running Locally

### **Terminal 1: Backend**

```bash
cd backend
mvn spring-boot:run  # Starts Spring Boot server on port 8000

# Expected output:
# INFO ... Application started in ... seconds
# INFO ... Tomcat started on port(s): 8000 ✓
```

**Check backend is working:**
```bash
# In new terminal:
curl http://localhost:8000/swagger-ui.html
# Should open Spring Boot Swagger UI in browser ✓
```

### **Terminal 2: Frontend**

```bash
cd frontend
npm run dev

# Expected output:
# VITE v5.X.X ready in 234 ms
# ➜  Local:   http://localhost:5173/
```

**Check frontend is working:**
```bash
# Open browser: http://localhost:5173/
# Should show React app ✓
```

### **Test Connection**

```bash
# Try submitting a dispute from frontend form
# Should:
1. Form validates input ✓
2. Submit button sends POST to http://localhost:8000/api/disputes ✓
3. Backend processes request ✓
4. Response shows dispute_id and neft_reference ✓

Troubleshoot:
├─ CORS error? → Add CORS middleware to FastAPI (see below)
├─ Port in use? → Use different port: --port 8001
├─ Module not found? → Run pip install again in venv
└─ npm error? → rm -rf node_modules && npm install
```

---

## 📝 Configuration Files

### **Backend: pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.0</version>
    </parent>

    <groupId>com.upi</groupId>
    <artifactId>dispute-api</artifactId>
    <version>1.0.0</version>
    <name>UPI Dispute Resolution API</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web (REST APIs) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA (Database ORM) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- SQLite JDBC Driver -->
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.44.0.0</version>
        </dependency>

        <!-- Lombok (reduce boilerplate) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### **Backend: application.properties**

```properties
# Server Configuration
server.port=8000
server.servlet.context-path=/

# Database Configuration (SQLite)
spring.datasource.url=jdbc:sqlite:disputes.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update

# JPA Configuration
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Application name
spring.application.name=UPI Dispute Resolution API

# CORS Configuration is in Java code (@Configuration class)
```

### **Backend: DemoApplication.java**

```java
package com.upi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Configuration
    public static class CorsConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        }
    }
}
```

### **Frontend: .env.local**

```env
VITE_API_URL=http://localhost:8000
VITE_APP_NAME="UPI Dispute Resolution"
```

### **Frontend: vite.config.js**

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      }
    }
  }
})
```

### **Frontend: tailwind.config.js**

```javascript
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

---

## 🧪 Testing Endpoints Locally

### **Using Postman**

1. Download Postman: https://www.postman.com/downloads/
2. Create new request:
   ```
   Method: POST
   URL: http://localhost:8000/api/disputes
   Body (JSON):
   {
     "transaction_id": "TXN20260227123456",
     "merchant_upi": "amazon@upi",
     "amount": 5000,
     "customer_phone": "+919876543210"
   }
   ```
3. Click Send → Should get 200 response with dispute_id

### **Using cURL**

```bash
# File dispute
curl -X POST http://localhost:8000/api/disputes \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_id": "TXN20260227123456",
    "merchant_upi": "amazon@upi",
    "amount": 5000,
    "customer_phone": "+919876543210"
  }'

# Check status (replace DIS_ID with actual dispute ID)
curl http://localhost:8000/api/disputes/DIS_1709028600
```

### **Using Browser**

```
1. Go to http://localhost:8000/docs
2. Click "POST /api/disputes"
3. Click "Try it out"
4. Enter test data
5. Click "Execute"
6. See response below
```

---

## 🐛 Troubleshooting

### **Backend Issues**

```
Problem: ModuleNotFoundError: No module named 'fastapi'
Solution: pip install fastapi uvicorn sqlalchemy pydantic

Problem: Port 8000 in use
Solution: Kill process or use different port
  On Mac/Linux: lsof -i :8000 | grep LISTEN | awk '{print $2}' | xargs kill -9
  On Windows: netstat -ano | findstr :8000 → taskkill /PID <PID>
  Or just use: uvicorn main:app --port 8001

Problem: CORS error in browser
Solution: Make sure CORS middleware added to FastAPI (see main.py above)

Problem: Import error in DB
Solution: Make sure you're in the backend/ directory and venv is activated
```

### **Frontend Issues**

```
Problem: npm ERR! Cannot find module 'react'
Solution: npm install

Problem: Module not found: react/jsx-runtime
Solution: rm -rf node_modules && npm install

Problem: Cannot find module axios
Solution: npm install axios

Problem: Port 5173 in use
Solution: Kill node process or use vite.config: server: { port: 5174 }
```

### **Connection Issues**

```
Problem: Frontend can't reach backend
Solution:
  1. Check backend is running: curl http://localhost:8000/health
  2. Check frontend is using correct URL: http://localhost:8000
  3. Check CORS is enabled in FastAPI
  4. Check no network proxy blocking requests

Problem: Form submits but no response
Solution:
  1. Check network tab in browser DevTools (F12)
  2. Check backend console for errors
  3. Make sure validation passes before submit
```

---

## 📦 Dependencies Summary

### **Backend (Maven)**

In `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

Install: `mvn clean install` (downloads all dependencies)

### **Frontend (npm)**

```
react==18.2.0
react-dom==18.2.0
vite==5.0.0
axios==1.6.2
recharts==2.10.0
tailwindcss==3.3.0
```

Install: `npm install react react-dom axios recharts tailwindcss`

---

## ✅ Setup Checklist

```
BEFORE YOU START CODING:

Backend:
├─ [ ] Python 3.11+ installed (python3 --version)
├─ [ ] Virtual environment created (python3.11 -m venv venv)
├─ [ ] Venv activated (source venv/bin/activate)
├─ [ ] Dependencies installed (pip install fastapi uvicorn sqlalchemy pydantic)
├─ [ ] main.py exists with CORS setup
├─ [ ] db.py exists with SQLite config
├─ [ ] Backend starts: uvicorn main:app --reload
├─ [ ] Swagger UI works: http://localhost:8000/docs
└─ [ ] Health check works: curl http://localhost:8000/health

Frontend:
├─ [ ] Node 16+ installed (node --version)
├─ [ ] npm 8+ installed (npm --version)
├─ [ ] React created: npm create vite@latest . -- --template react
├─ [ ] Dependencies installed: npm install axios recharts tailwindcss
├─ [ ] Dev server starts: npm run dev
├─ [ ] App loads: http://localhost:5173/
├─ [ ] Tailwind CSS loaded (check global styles)
└─ [ ] Can see browser DevTools (F12)

Integration:
├─ [ ] Backend running on port 8000
├─ [ ] Frontend running on port 5173
├─ [ ] CORS error check (should NOT see CORS error)
└─ [ ] Test form submission (should POST to backend)

If all checked: ✅ Ready to code!
```

---

## 🚀 Next Step

👉 Read **[06_IMPLEMENTATION_GUIDE.md](06_IMPLEMENTATION_GUIDE.md)** to start coding
