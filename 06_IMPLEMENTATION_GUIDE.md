# 06_IMPLEMENTATION_GUIDE (Java + Spring Boot)

## 🎯 Build Step-by-Step

Complete all code in the sections below. Copy-paste directly into your files.

---

## 📍 Step 1: Backend Models & Database

### **File: src/main/java/com/upi/model/Dispute.java**

```java
package com.upi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "disputes", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dispute {

    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String merchantUpi;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private Double amount;

    // Status tracking
    @Column(nullable = false)
    private String status = "PENDING";

    private String approvalStatus;

    private Double confidenceScore = 0.0;

    // Bank verification
    private Boolean customerBankDebited;

    private Double customerBankAmount;

    @Column(length = 2000)
    private String customerBankResponse;

    private Boolean merchantBankCredited;

    private Double merchantBankAmount;

    @Column(length = 2000)
    private String merchantBankResponse;

    // Refund
    private String neftReference;

    private Double refundAmount;

    // Timestamps
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime verifiedAt;

    private LocalDateTime refundInitiatedAt;

    private LocalDateTime expectedSettlementAt;

    private LocalDateTime settledAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## 📍 Step 2: Backend Repository & DTOs

### **File: src/main/java/com/upi/repository/DisputeRepository.java**

```java
package com.upi.repository;

import com.upi.model.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, String> {
    Dispute findByTransactionId(String transactionId);
}
```

### **File: src/main/java/com/upi/dto/DisputeRequest.java**

```java
package com.upi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeRequest {

    @NotBlank(message = "Transaction ID is required")
    @Pattern(regexp = "^TXN\\d{14,}$", message = "Transaction ID must be TXN followed by 14+ digits")
    private String transactionId;

    @NotBlank(message = "Merchant UPI is required")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z]+$", message = "Invalid UPI format")
    private String merchantUpi;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least ₹1")
    @Max(value = 100000, message = "Amount cannot exceed ₹100,000")
    private Double amount;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+91\\d{10}$", message = "Phone must be +91 followed by 10 digits")
    private String customerPhone;
}
```

### **File: src/main/java/com/upi/dto/DisputeResponse.java**

```java
package com.upi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeResponse {
    private Boolean success;
    private String disputeId;
    private String status;
    private String neftReference;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
}
```

---

## 📍 Step 3: Bank API Service

### **File: src/main/java/com/upi/service/BankApiService.java**

```java
package com.upi.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class BankApiService {

    private static final Random random = new Random();

    public BankVerificationResult verifyWithBanks(
            String transactionId,
            String merchantUpi,
            Double amount) throws InterruptedException {

        // Simulate parallel API calls with Thread.start() or use CompletableFuture
        BankVerificationResult result = new BankVerificationResult();

        // Simulate customer bank call (1-2 seconds)
        Thread.sleep((long) (1000 + random.nextDouble() * 1000));
        result.setCustomerBankDebited(true);  // Assume money left customer account

        // Simulate merchant bank call (1-2 seconds)
        Thread.sleep((long) (1000 + random.nextDouble() * 1000));
        // 95% of cases: money didn't reach; 5% false claims
        boolean didNotReach = random.nextDouble() < 0.95;
        result.setMerchantBankCredited(!didNotReach);

        return result;
    }

    public static class BankVerificationResult {
        private Boolean customerBankDebited;
        private Boolean merchantBankCredited;

        public Boolean getCustomerBankDebited() {
            return customerBankDebited;
        }

        public void setCustomerBankDebited(Boolean customerBankDebited) {
            this.customerBankDebited = customerBankDebited;
        }

        public Boolean getMerchantBankCredited() {
            return merchantBankCredited;
        }

        public void setMerchantBankCredited(Boolean merchantBankCredited) {
            this.merchantBankCredited = merchantBankCredited;
        }
    }
}
```

---

## 📍 Step 4: Backend Service (Business Logic)

### **File: src/main/java/com/upi/service/DisputeService.java**

```java
package com.upi.service;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.model.Dispute;
import com.upi.repository.DisputeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DisputeService {

    @Autowired
    private DisputeRepository disputeRepository;

    @Autowired
    private BankApiService bankApiService;

    public DisputeResponse fileDispute(DisputeRequest request) throws InterruptedException {
        // Check for duplicate
        if (disputeRepository.findByTransactionId(request.getTransactionId()) != null) {
            throw new RuntimeException("Dispute already filed for this transaction");
        }

        // Create new dispute
        Dispute dispute = new Dispute();
        String disputeId = "DIS_" + System.currentTimeMillis();
        dispute.setId(disputeId);
        dispute.setTransactionId(request.getTransactionId());
        dispute.setMerchantUpi(request.getMerchantUpi());
        dispute.setCustomerPhone(request.getCustomerPhone());
        dispute.setAmount(request.getAmount());
        dispute.setStatus("PENDING");

        // Save initial record
        disputeRepository.save(dispute);

        // Verify with banks
        try {
            BankApiService.BankVerificationResult bankResult = bankApiService.verifyWithBanks(
                request.getTransactionId(),
                request.getMerchantUpi(),
                request.getAmount()
            );

            dispute.setCustomerBankDebited(bankResult.getCustomerBankDebited());
            dispute.setMerchantBankCredited(bankResult.getMerchantBankCredited());
            dispute.setVerifiedAt(LocalDateTime.now());

            // Decide: Is this a verified failure?
            String message;
            String neftRef = null;

            if (bankResult.getCustomerBankDebited() && !bankResult.getMerchantBankCredited()) {
                // Verified failure: money left, didn't reach
                dispute.setStatus("VERIFIED_FAILURE");
                dispute.setApprovalStatus("AUTO_APPROVED");
                dispute.setConfidenceScore(0.99);

                // Initiate refund
                neftRef = "NEFT" + System.currentTimeMillis();
                dispute.setNeftReference(neftRef);
                dispute.setRefundAmount(request.getAmount());
                dispute.setRefundInitiatedAt(LocalDateTime.now());
                dispute.setExpectedSettlementAt(LocalDateTime.now().plusDays(1));
                dispute.setStatus("REFUND_INITIATED");

                message = "Refund initiated. Money by tomorrow 9 AM";

            } else if (bankResult.getCustomerBankDebited() && bankResult.getMerchantBankCredited()) {
                // False claim: money did reach
                dispute.setStatus("REJECTED");
                dispute.setApprovalStatus("REJECTED");
                dispute.setConfidenceScore(0.95);
                message = "We verified that money already reached merchant";

            } else {
                // Unclear case
                dispute.setStatus("MANUAL_REVIEW");
                dispute.setApprovalStatus("MANUAL_REVIEW");
                dispute.setConfidenceScore(0.50);
                message = "Case needs manual investigation. Our team will review within 24h";
            }

            dispute.setUpdatedAt(LocalDateTime.now());
            disputeRepository.save(dispute);

            return new DisputeResponse(
                dispute.getStatus().equals("REJECTED") ? false : true,
                dispute.getId(),
                dispute.getStatus(),
                neftRef != null ? neftRef : "",
                message,
                dispute.getCreatedAt(),
                dispute.getVerifiedAt()
            );

        } catch (Exception e) {
            // Bank API error: escalate for manual review
            dispute.setStatus("MANUAL_REVIEW");
            dispute.setApprovalStatus("MANUAL_REVIEW");
            dispute.setUpdatedAt(LocalDateTime.now());
            disputeRepository.save(dispute);

            return new DisputeResponse(
                true,
                dispute.getId(),
                "MANUAL_REVIEW",
                "",
                "Verification delayed. Our team will review your case",
                dispute.getCreatedAt(),
                null
            );
        }
    }

    public Dispute getDisputeStatus(String disputeId) {
        Optional<Dispute> dispute = disputeRepository.findById(disputeId);
        if (dispute.isEmpty()) {
            throw new RuntimeException("Dispute not found");
        }
        return dispute.get();
    }
}
```

---

## 📍 Step 5: Backend Controller (REST API)

### **File: src/main/java/com/upi/controller/DisputeController.java**

```java
package com.upi.controller;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.model.Dispute;
import com.upi.service.DisputeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/disputes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class DisputeController {

    @Autowired
    private DisputeService disputeService;

    @PostMapping
    public ResponseEntity<?> fileDispute(@Valid @RequestBody DisputeRequest request) {
        try {
            DisputeResponse response = disputeService.fileDispute(request);
            if ("REJECTED".equals(response.getStatus())) {
                return ResponseEntity.status(422).body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                .body(new ErrorResponse(false, "ERROR", e.getMessage()));
        }
    }

    @GetMapping("/{disputeId}")
    public ResponseEntity<?> getStatus(@PathVariable String disputeId) {
        try {
            Dispute dispute = disputeService.getDisputeStatus(disputeId);
            return ResponseEntity.ok(dispute);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(new ErrorResponse(false, "NOT_FOUND", "Dispute not found"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new HealthResponse("ok"));
    }

    // Inner classes for responses
    static class ErrorResponse {
        public Boolean success;
        public String error;
        public String message;

        ErrorResponse(Boolean success, String error, String message) {
            this.success = success;
            this.error = error;
            this.message = message;
        }
    }

    static class HealthResponse {
        public String status;

        HealthResponse(String status) {
            this.status = status;
        }
    }
}
```

---

## 📍 Step 6: Frontend (Same as Python version)

Frontend code remains the same (React + Axios):

### **File: frontend/src/components/DisputeForm.jsx**

[Same as `06_IMPLEMENTATION_GUIDE.md` - copy the DisputeForm.jsx section]

### **File: frontend/src/components/StatusPage.jsx**

[Same as `06_IMPLEMENTATION_GUIDE.md` - copy the StatusPage.jsx section]

### **File: frontend/src/App.jsx**

[Same as `06_IMPLEMENTATION_GUIDE.md` - copy the App.jsx section]

---

## ✅ Testing Your Implementation

### **Test 1: Backend Starts**

```bash
cd backend
mvn spring-boot:run

# Expected:
# INFO ... Application started in ... seconds
# INFO ... Tomcat started on port(s): 8000 ✓
```

### **Test 2: Frontend Starts**

```bash
cd frontend
npm run dev

# Expected:
# VITE v5.0.0 ready in 234 ms
# ➜  Local:   http://localhost:5173/ ✓
```

### **Test 3: File a Dispute**

```bash
# In browser, go to http://localhost:5173/
# Fill form:
# - Transaction ID: TXN20260227123456
# - Merchant UPI: amazon@upi
# - Amount: 5000
# - Phone: +919876543210
# Click Submit

# Expected:
# - Form validates (shows errors if invalid)
# - Page changes to status page
# - Shows "✅ Refund Initiated!"
# - Displays dispute_id and neft_reference
```

### **Test 4: Check Status**

```bash
curl http://localhost:8000/api/disputes/DIS_XXXX

# Expected: Full dispute details with status
```

---

## 🎯 What You Now Have

✅ Complete Spring Boot backend with Java
✅ Complete frontend with React + Tailwind CSS
✅ Database schema (disputes table with SQLite)
✅ Bank API verification (simulated with threading)
✅ Dispute filing flow
✅ Status tracking
✅ Full error handling and validation

**Next:** Read [07_ROADMAP_AND_TIMELINE.md](07_ROADMAP_AND_TIMELINE.md) for how to complete optional features
