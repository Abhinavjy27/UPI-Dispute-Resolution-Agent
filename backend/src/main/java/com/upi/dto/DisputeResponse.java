package com.upi.dto;

import java.time.LocalDateTime;

public class DisputeResponse {
    private Long id;
    private String disputeId;
    private String transactionId;
    private String merchantUPI;
    private Double amount;
    private String phone;
    private String status;
    private String neftReference;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;

    // Constructors
    public DisputeResponse() {}

    public DisputeResponse(Long id, String disputeId, String transactionId, String merchantUPI, Double amount,
                          String phone, String status, String neftReference, String remarks,
                          LocalDateTime createdAt, LocalDateTime verifiedAt) {
        this.id = id;
        this.disputeId = disputeId;
        this.transactionId = transactionId;
        this.merchantUPI = merchantUPI;
        this.amount = amount;
        this.phone = phone;
        this.status = status;
        this.neftReference = neftReference;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.verifiedAt = verifiedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(String disputeId) {
        this.disputeId = disputeId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNeftReference() {
        return neftReference;
    }

    public void setNeftReference(String neftReference) {
        this.neftReference = neftReference;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
