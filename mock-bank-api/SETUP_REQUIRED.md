# ⚠️ Setup Required: Java & Maven Not Found

## Current Status
- ❌ Java: Not installed or not in PATH
- ❌ Maven: Not installed or not in PATH

## Required Setup

### Step 1: Install Java 17+

**Windows**:
1. Download from: https://www.oracle.com/java/technologies/downloads/#java17
2. Run the installer and follow the prompts
3. After installation, **restart your terminal**

**Or use Windows Package Manager**:
```powershell
winget install Oracle.JDK.17
```

### Step 2: Install Maven

**Windows - Option A: Direct Download**:
1. Download from: https://maven.apache.org/download.cgi
2. Extract to: `C:\Program Files\apache-maven-3.9.0` (or similar)
3. Add to Environment Variables:
   - Variable: `MAVEN_HOME` = `C:\Program Files\apache-maven-3.9.0`
   - Add to PATH: `%MAVEN_HOME%\bin`

**Windows - Option B: Use Chocolatey** (if installed):
```powershell
choco install maven
```

**Windows - Option C: Use Windows Package Manager**:
```powershell
winget install Maven.Maven
```

### Step 3: Verify Installation

After restart, test:
```powershell
java -version
mvn -version
```

Both should show version information.

## Then Run the Project

Once Java and Maven are installed:

```powershell
cd c:\Users\Hasin\Downloads\UPI-Dispute-Resolution-Agent\mock-bank-api

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

## Alternative: Download Pre-built JAR

If you want to skip Maven build, we can create a pre-built JAR file. Contact for that option.

## Quick Reference Links

- **Java Downloads**: https://www.oracle.com/java/technologies/downloads/
- **Maven Downloads**: https://maven.apache.org/download.cgi
- **Java Installation Guide**: https://docs.oracle.com/en/java/javase/17/install/overview.html
- **Maven Installation Guide**: https://maven.apache.org/install.html

---

**Next Steps**:
1. Install Java 17+
2. Install Maven 3.6+
3. Restart terminal
4. Run `mvn clean package` from the project directory
5. Run `mvn spring-boot:run`

**Need help?** Check the installation guides above or the project's QUICK_START.md file.
