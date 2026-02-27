package com.upi.scheduler;

import com.upi.model.Dispute;
import com.upi.repository.DisputeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class DisputeAutoApprovalScheduler {
    private static final Logger logger = Logger.getLogger(DisputeAutoApprovalScheduler.class.getName());
    private final DisputeRepository disputeRepository;

    // Auto-approve after 2 hours for demo (adjust as needed)
    private static final long AUTO_APPROVE_HOURS = 2;

    public DisputeAutoApprovalScheduler(DisputeRepository disputeRepository) {
        this.disputeRepository = disputeRepository;
    }

    /**
     * Scheduled task that runs every 5 minutes to check for disputes
     * that need auto-approval
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000) // Every 5 minutes, start after 1 minute
    public void autoApproveDisputes() {
        logger.info("Running auto-approval check for disputes...");
        
        try {
            // Find all disputes in MANUAL_REVIEW status
            List<Dispute> manualReviewDisputes = disputeRepository.findAll().stream()
                .filter(d -> d.getStatus() == Dispute.DisputeStatus.MANUAL_REVIEW)
                .toList();
            
            if (manualReviewDisputes.isEmpty()) {
                logger.info("No disputes pending manual review");
                return;
            }
            
            logger.info("Found " + manualReviewDisputes.size() + " disputes in manual review");
            
            int approvedCount = 0;
            LocalDateTime now = LocalDateTime.now();
            
            for (Dispute dispute : manualReviewDisputes) {
                long hoursElapsed = ChronoUnit.HOURS.between(dispute.getCreatedAt(), now);
                
                if (hoursElapsed >= AUTO_APPROVE_HOURS) {
                    // Auto-approve the dispute
                    dispute.setStatus(Dispute.DisputeStatus.VERIFIED_FAILURE);
                    dispute.setNeftReference("NEFT" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                    dispute.setRemarks("Auto-approved after manual review period. Refund initiated.");
                    dispute.setVerifiedAt(now);
                    
                    disputeRepository.save(dispute);
                    approvedCount++;
                    
                    logger.info("Auto-approved dispute: " + dispute.getId() + 
                               " (Transaction: " + dispute.getTransactionId() + 
                               ") - NEFT: " + dispute.getNeftReference());
                }
            }
            
            if (approvedCount > 0) {
                logger.info("Auto-approved " + approvedCount + " dispute(s) successfully");
            } else {
                logger.info("No disputes ready for auto-approval yet");
            }
            
        } catch (Exception e) {
            logger.severe("Error during auto-approval process: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
