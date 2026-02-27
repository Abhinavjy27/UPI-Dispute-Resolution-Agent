# Quick Start Guide - Mock Bank API

## Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher
- **Git**: For version control (optional)

## Installation & Running

### 1. Navigate to Project Directory
```bash
cd mock-bank-api
```

### 2. Build the Project
```bash
mvn clean package
```

This command will:
- Clean previous builds
- Download dependencies
- Compile source code
- Run tests
- Create JAR file

### 3. Run the Application

**Option A: Using Maven**
```bash
mvn spring-boot:run
```

**Option B: Using Java JAR**
```bash
java -jar target/mock-bank-api-1.0.0.jar
```

### 4. Verify Application is Running

The application should start on `http://localhost:8080`

**Check Application Status**:
```bash
curl http://localhost:8080/swagger-ui.html
```

You should see the Swagger UI documentation page.

---

## Accessing the Application

### API Endpoints
- **Base URL**: `http://localhost:8080`
- **API Documentation**: `http://localhost:8080/swagger-ui.html`
- **Database Console**: `http://localhost:8080/h2-console`

### Sample Transactions (Pre-loaded)
The following transactions are automatically created on startup:
- `TXN20240101001` - SUCCESS ($1000.00)
- `TXN20240101002` - SUCCESS ($500.50)
- `TXN20240101003` - FAILED ($250.00)
- `TXN20240101004` - SUCCESS ($2000.00)
- `TXN20240101005` - FAILED ($150.00)

---

## First API Call

### Get a Sample Transaction

```bash
curl -X GET http://localhost:8080/bank/transaction/TXN20240101001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

**Expected Response**:
```json
{
  "success": true,
  "message": "Transaction retrieved successfully",
  "data": {
    "transaction_id": "TXN20240101001",
    "amount": 1000.0,
    "status": "SUCCESS",
    "timestamp": "2024-02-27T10:45:30.123456",
    "payer_id": "CUST001",
    "payee_id": "MERCHANT001",
    "description": "Payment for grocery shopping"
  },
  "timestamp": "2024-02-27T10:45:35.654321"
}
```

---

## Create Your First Transaction

```bash
curl -X POST http://localhost:8080/bank/transaction \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "MY_FIRST_TXN",
    "amount": 2500.00,
    "status": "SUCCESS",
    "payer_id": "MY_CUSTOMER",
    "payee_id": "MY_MERCHANT",
    "description": "My first test transaction"
  }'
```

---

## Process Your First Refund

```bash
curl -X POST http://localhost:8080/bank/refund \
  -H "Content-Type: application/json" \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024" \
  -d '{
    "transaction_id": "MY_FIRST_TXN",
    "reason": "Testing refund functionality"
  }'
```

---

## File Structure

```
mock-bank-api/
├── src/main/
│   ├── java/com/mockbank/
│   │   ├── MockBankApiApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── exception/
│   │   └── security/
│   └── resources/
│       └── application.yml
├── pom.xml
├── README.md
├── QUICK_START.md (this file)
├── CURL_COMMANDS.md
└── INTEGRATION_GUIDE.md
```

---

## Common Commands

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

### Skip Tests (faster build)
```bash
mvn clean package -DskipTests
```

### View Help
```bash
mvn spring-boot:help
```

---

## Configuration

### Default Configuration
```yaml
server:
  port: 8080

app:
  api-key: upi-dispute-resolver-secret-key-2024

spring:
  datasource:
    url: jdbc:h2:mem:mockbankdb
```

### Changing Port
Edit `src/main/resources/application.yml`:
```yaml
server:
  port: 8090  # Change to your desired port
```

### Changing API Key
Edit `src/main/resources/application.yml`:
```yaml
app:
  api-key: "your-new-api-key-here"
```

---

## Accessing H2 Database Console

1. Open browser: `http://localhost:8080/h2-console`
2. Keep default settings:
   - Driver Class: `org.h2.Driver`
   - JDBC URL: `jdbc:h2:mem:mockbankdb`
   - User Name: `sa`
   - Password: (leave empty)
3. Click "Connect"

You can now run SQL queries to inspect the data:
```sql
SELECT * FROM transactions;
SELECT * FROM refunds;
```

---

## Useful Maven Commands

```bash
# Clean build directory
mvn clean

# Compile only (no tests)
mvn compile

# Run tests
mvn test

# Package without running tests
mvn package -DskipTests

# Check dependency tree
mvn dependency:tree

# Download sources
mvn dependency:sources

# View all goals
mvn help:describe -Dplugin=spring-boot
```

---

## Troubleshooting

### Issue: Port 8080 is already in use
**Solution**: Change port in `application.yml` or kill the process using port 8080

```bash
# On Windows (PowerShell)
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process

# On Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Issue: "No such file or directory: target/..."
**Solution**: Run `mvn clean package` first to build the project

### Issue: Java version not compatible
**Solution**: Ensure you have Java 17+
```bash
java -version
```

### Issue: Maven command not found
**Solution**: 
1. Install Maven from https://maven.apache.org/
2. Add Maven to your PATH environment variable

### Issue: 401 Unauthorized on API calls
**Solution**: Ensure you're sending the correct API key header:
```bash
-H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

---

## Next Steps

1. **Review Documentation**
   - Read `README.md` for comprehensive overview
   - Check `CURL_COMMANDS.md` for all API examples
   - See `INTEGRATION_GUIDE.md` for Python integration

2. **Test All Endpoints**
   - Use Swagger UI at `/swagger-ui.html`
   - Run sample cURL commands

3. **Integrate with Python Agent**
   - Follow `INTEGRATION_GUIDE.md`
   - Test Python code examples

4. **Customize**
   - Modify sample data in `DataInitializer.java`
   - Change API key in `application.yml`
   - Adjust business logic in `Service` classes

---

## Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Complete project documentation |
| `QUICK_START.md` | This file - quick getting started guide |
| `CURL_COMMANDS.md` | All cURL command examples |
| `INTEGRATION_GUIDE.md` | Python agent integration guide |

---

## Getting Help

### View Logs
```bash
# When running with Maven
mvn spring-boot:run

# Look for log output showing:
# - Started MockBankApiApplication
# - Application ready to serve requests
```

### Check API Health
```bash
# Try fetching a sample transaction
curl http://localhost:8080/bank/transaction/TXN20240101001 \
  -H "x-api-key: upi-dispute-resolver-secret-key-2024"
```

### View Swagger Documentation
Open in browser: `http://localhost:8080/swagger-ui.html`

---

## Tips

1. **Use Postman or Insomnia**: For easier API testing with GUI
2. **Store API Key in Variable**: `API_KEY="upi-dispute-resolver-secret-key-2024"`
3. **Pretty Print JSON**: Pipe to `jq` or Python's `json.tool`
4. **Check H2 Console**: View actual database data in browser

---

## Support

For more details, refer to:
- `README.md` - Full documentation
- `CURL_COMMANDS.md` - API command examples
- `INTEGRATION_GUIDE.md` - Python integration details

Good luck with your UPI Dispute Resolution Agent! 🚀
