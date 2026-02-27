package com.upi.dto;

import jakarta.validation.constraints.*;

public class DisputeRequest {
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Merchant UPI is required")
    private String merchantUPI;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    private String reason;

    // Constructors
    public DisputeRequest() {}

    public DisputeRequest(String transactionId, String merchantUPI, Double amount, String phone, String reason) {
        this.transactionId = transactionId;
        this.merchantUPI = merchantUPI;
        this.amount = amount;
        this.phone = phone;
        this.reason = reason;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantUPI() {
        return merchantUPI;
    }

    public void setMerchantUPI(String merchantUPI) {
        this.merchantUPI = merchantUPI;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
