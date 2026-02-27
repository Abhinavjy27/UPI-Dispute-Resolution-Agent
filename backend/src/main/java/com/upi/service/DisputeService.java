package com.upi.service;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.model.Dispute;
import com.upi.repository.DisputeRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

@Service
public class DisputeService {
    private static final Logger logger = Logger.getLogger(DisputeService.class.getName());
    private final DisputeRepository disputeRepository;

    public DisputeService(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
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
        String verificationResult = callBankVerificationAPI(request.getTransactionId());
        logger.info("Bank verification result: " + verificationResult);

        // Decide dispute outcome
        boolean isVerifiedFailure = verificationResult.equals("VERIFIED_FAILURE");
        if (isVerifiedFailure) {
            dispute.setStatus(Dispute.DisputeStatus.VERIFIED_FAILURE);
            dispute.setNeftReference("NEFT" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            dispute.setRemarks("Transaction verified as failed. Refund initiated.");
            logger.info("Refund initiated with NEFT reference: " + dispute.getNeftReference());
        } else {
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

    private String callBankVerificationAPI(String transactionId) {
        try {
            // Simulate API call with 2 second delay
            Thread.sleep(2000);
            logger.info("Mock bank API called for transaction: " + transactionId);
            
            // Random outcome: 70% verified failure, 30% false claim
            return Math.random() < 0.7 ? "VERIFIED_FAILURE" : "FALSE_CLAIM";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.severe("Bank API call interrupted: " + e.getMessage());
            return "UNKNOWN";
        }
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
