# ⚠️ CRITICAL: Java 17+ Required

## Current Status
- ✅ Maven installed at: `C:\Users\Hasin\Downloads\apache-maven-3.9.12-bin\apache-maven-3.9.12`
- ❌ **Java 17+ NOT installed** ← **REQUIRED FIRST!**

## Why You Need Java First

Maven is a build tool that **requires Java to run**. You must install Java 17+ before Maven will work.

---

## Install Java 17+ NOW

### **Option 1: Fastest - Use WinGet** (Recommended)
```powershell
winget install Oracle.JDK.17
```

### **Option 2: Manual Download**
1. Download from: https://www.oracle.com/java/technologies/downloads/#java17
2. Run installer
3. Choose: `C:\Program Files\Java\jdk-17.x.x`
4. Complete installation

### **Option 3: Use Chocolatey**
```powershell
choco install jdk17
```

---

## After Installing Java

### 1. Set JAVA_HOME Environment Variable
```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17.0.x", [EnvironmentVariableTarget]::User)
```

Find your Java version in `C:\Program Files\Java\` and use the exact folder name.

### 2. Add Maven to PATH
```powershell
[Environment]::SetEnvironmentVariable("Path", "$env:Path;C:\Users\Hasin\Downloads\apache-maven-3.9.12-bin\apache-maven-3.9.12\bin", [EnvironmentVariableTarget]::User)
```

### 3. **Restart PowerShell** (Important!)

### 4. Verify Both Work
```powershell
java -version
mvn -version
```

Both should show version numbers.

---

## Then Build & Run the API

```powershell
cd c:\Users\Hasin\Downloads\UPI-Dispute-Resolution-Agent\mock-bank-api
mvn clean package -DskipTests
mvn spring-boot:run
```

---

## Status After Java Install

```
✅ Java 17+        → Download & Install
✅ Maven 3.9.12    → Already installed
✅ JAVA_HOME       → Set to Java directory
✅ PATH            → Updated with Maven & Java
✅ Restart shell   → Close and reopen PowerShell
```

**Then your API will build and run!** 🚀
