package com.upi.service;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.model.Dispute;
import com.upi.repository.DisputeRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class DisputeService {
    private static final Logger logger = Logger.getLogger(DisputeService.class.getName());
    private static final String MOCK_BANK_API_URL = "http://host.docker.internal:8080";
    private static final String MOCK_BANK_API_KEY = "upi-dispute-resolver-secret-key-2024";
    private static final double HIGH_AMOUNT_THRESHOLD = 5000.0;
    
    private final DisputeRepository disputeRepository;
    private final RestTemplate restTemplate;

    public DisputeService(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
        this.restTemplate = new RestTemplate();
    }

    public DisputeResponse fileDispute(DisputeRequest request) {
        logger.info("Filing dispute for transaction: " + request.getTransactionId());

        // Check for duplicates
        Optional<Dispute> existing = disputeRepository.findByTransactionId(request.getTransactionId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Dispute already filed for this transaction");
        }

        // Create new dispute
        Dispute dispute = new Dispute();
        dispute.setTransactionId(request.getTransactionId());
        dispute.setMerchantUPI(request.getMerchantUPI());
        dispute.setAmount(request.getAmount());
        dispute.setPhone(request.getPhone());
        dispute.setReason(request.getReason());

        // Simulate bank verification API call (2 second delay)
        String verificationResult = callBankVerificationAPI(request.getTransactionId(), request.getAmount());
        logger.info("Bank verification result: " + verificationResult);

        // Decide dispute outcome based on amount and verification
        if (verificationResult.equals("VERIFIED_FAILURE")) {
            // Small amount disputes - auto approve immediately
            dispute.setStatus(Dispute.DisputeStatus.VERIFIED_FAILURE);
            dispute.setNeftReference("NEFT" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            dispute.setRemarks("Transaction verified as failed. Refund initiated.");
            logger.info("Refund initiated with NEFT reference: " + dispute.getNeftReference());
        } else if (verificationResult.equals("FALSE_CLAIM")) {
            // Transaction was successful - reject the claim
            dispute.setStatus(Dispute.DisputeStatus.FALSE_CLAIM);
            dispute.setRemarks("Transaction completed successfully. No refund applicable.");
            logger.info("False claim detected - Dispute rejected");
        } else if (verificationResult.equals("HIGH_AMOUNT")) {
            // High amount disputes - send to manual review (will be auto-approved by scheduler in 5 seconds)
            dispute.setStatus(Dispute.DisputeStatus.MANUAL_REVIEW);
            dispute.setRemarks("High value transaction - pending review. Will be processed shortly.");
        } else {
            // Other cases - manual review
            dispute.setStatus(Dispute.DisputeStatus.MANUAL_REVIEW);
            dispute.setRemarks("Requires manual review by bank");
        }

        // Save to database
        dispute = disputeRepository.save(dispute);

        return mapToResponse(dispute);
    }

    public DisputeResponse getDisputeStatus(Long disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
            .orElseThrow(() -> new IllegalArgumentException("Dispute not found"));
        return mapToResponse(dispute);
    }

    public List<DisputeResponse> getUserDisputes(String phone) {
        logger.info("Fetching disputes for phone: " + phone);
        List<Dispute> disputes = disputeRepository.findByPhone(phone);
        return disputes.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserDisputes(String phone) {
        logger.info("Deleting all disputes for phone: " + phone);
        disputeRepository.deleteByPhone(phone);
        logger.info("Successfully deleted disputes for phone: " + phone);
    }

    private String callBankVerificationAPI(String transactionId, Double disputeAmount) {
        try {
            logger.info("Calling mock bank API for transaction: " + transactionId);
            
            // Call actual mock bank API
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", MOCK_BANK_API_KEY);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            String url = MOCK_BANK_API_URL + "/bank/transaction/" + transactionId;
            ResponseEntity<BankApiResponse> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                BankApiResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getData() != null) {
                BankTransactionData bankData = response.getBody().getData();
                logger.info("Bank API response - Status: " + bankData.getStatus() + ", Amount: " + bankData.getAmount());
                
                // Check if transaction failed in bank
                if ("FAILED".equalsIgnoreCase(bankData.getStatus())) {
                    logger.info("Transaction failed in bank - Processing refund");
                    
                    // Check if amounts match (with tolerance for floating point)
                    if (Math.abs(bankData.getAmount() - disputeAmount) > 0.01) {
                        logger.warning("Amount mismatch! Bank: " + bankData.getAmount() + ", Dispute: " + disputeAmount);
                        return "AMOUNT_MISMATCH";
                    }
                    
                    // For high amounts, send to manual review
                    if (disputeAmount >= HIGH_AMOUNT_THRESHOLD) {
                        logger.info("High amount failed transaction (>=" + HIGH_AMOUNT_THRESHOLD + ") - Sending to manual review");
                        return "HIGH_AMOUNT";
                    }
                    
                    // Low amount failed transaction - auto approve refund
                    logger.info("Transaction verified as failed - Auto approving refund");
                    return "VERIFIED_FAILURE";
                }
                
                // Transaction succeeded - this is a false claim!
                logger.warning("Transaction was successful - Rejecting false claim");
                return "FALSE_CLAIM";
            }
            
            logger.warning("Bank API returned non-success status: " + response.getStatusCode());
            return "UNKNOWN";
            
        } catch (Exception e) {
            logger.severe("Bank API call failed: " + e.getMessage());
            e.printStackTrace();
            // On API failure, approve small amounts, review large amounts
            return disputeAmount < HIGH_AMOUNT_THRESHOLD ? "VERIFIED_FAILURE" : "HIGH_AMOUNT";
        }
    }
    
    // Inner class for bank API response wrapper
    private static class BankApiResponse {
        private boolean success;
        private String message;
        private BankTransactionData data;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public BankTransactionData getData() { return data; }
        public void setData(BankTransactionData data) { this.data = data; }
    }
    
    // Inner class for actual transaction data
    private static class BankTransactionData {
        private String transaction_id;
        private Double amount;
        private String status;
        private String timestamp;
        private String payer_id;
        private String payee_id;
        private String description;
        
        public String getTransaction_id() { return transaction_id; }
        public void setTransaction_id(String transaction_id) { this.transaction_id = transaction_id; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getPayer_id() { return payer_id; }
        public void setPayer_id(String payer_id) { this.payer_id = payer_id; }
        
        public String getPayee_id() { return payee_id; }
        public void setPayee_id(String payee_id) { this.payee_id = payee_id; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    private DisputeResponse mapToResponse(Dispute dispute) {
        DisputeResponse response = new DisputeResponse();
        response.setId(dispute.getId());
        response.setDisputeId("DIS_" + String.format("%06d", dispute.getId()));
        response.setTransactionId(dispute.getTransactionId());
        response.setMerchantUPI(dispute.getMerchantUPI());
        response.setAmount(dispute.getAmount());
        response.setPhone(dispute.getPhone());
        response.setStatus(dispute.getStatus().toString());
        response.setNeftReference(dispute.getNeftReference());
        response.setRemarks(dispute.getRemarks());
        response.setCreatedAt(dispute.getCreatedAt());
        response.setVerifiedAt(dispute.getVerifiedAt());
        return response;
    }
}
