package com.mockbank.service;

import com.mockbank.dto.RefundRequest;
import com.mockbank.dto.RefundResponse;
import com.mockbank.entity.Refund;
import com.mockbank.entity.Transaction;
import com.mockbank.exception.InvalidRefundException;
import com.mockbank.exception.TransactionNotFoundException;
import com.mockbank.repository.RefundRepository;
import com.mockbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Refund operations
 * Handles business logic for refund transactions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefundService {

    private final RefundRepository refundRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    /**
     * Process refund for a transaction
     * Business Logic:
     * - If status is SUCCESS: Create refund record and update transaction to REFUNDED
     * - If status is FAILED: Return error (Refund not allowed)
     * - If transaction not found: Return 404
     *
     * @param request RefundRequest containing transaction ID
     * @return RefundResponse with refund details
     * @throws TransactionNotFoundException if transaction not found
     * @throws InvalidRefundException if refund is not allowed for transaction status
     */
    public RefundResponse processRefund(RefundRequest request) {
        log.info("Processing refund for transaction ID: {}", request.getTransactionId());

        // Fetch the transaction
        Transaction transaction = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> {
                    log.warn("Refund request: Transaction not found with ID: {}", request.getTransactionId());
                    return new TransactionNotFoundException("Transaction not found with ID: " + request.getTransactionId());
                });

        // Check transaction status
        if ("FAILED".equals(transaction.getStatus())) {
            log.warn("Refund not allowed for FAILED transaction: {}", request.getTransactionId());
            throw new InvalidRefundException("Refund not allowed for FAILED transactions");
        }

        if ("REFUNDED".equals(transaction.getStatus())) {
            log.warn("Transaction already refunded: {}", request.getTransactionId());
            throw new InvalidRefundException("Transaction already refunded");
        }

        if (!"SUCCESS".equals(transaction.getStatus())) {
            log.warn("Cannot refund transaction with status: {}", transaction.getStatus());
            throw new InvalidRefundException("Can only refund transactions with SUCCESS status");
        }

        // Create refund record
        Refund refund = new Refund();
        refund.setTransactionId(request.getTransactionId());
        refund.setAmount(transaction.getAmount());
        refund.setRefundTimestamp(LocalDateTime.now());
        refund.setStatus("SUCCESS");
        refund.setReason(request.getReason());

        Refund savedRefund = refundRepository.save(refund);
        log.info("Refund record created with ID: {}", savedRefund.getRefundId());

        // Update transaction status to REFUNDED
        transactionService.updateTransactionStatus(request.getTransactionId(), "REFUNDED");
        log.info("Transaction status updated to REFUNDED");

        return convertToResponse(savedRefund);
    }

    /**
     * Fetch all refunds
     * @return List of all refunds
     */
    public List<RefundResponse> getAllRefunds() {
        log.info("Fetching all refunds");
        return refundRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Fetch refunds for a specific transaction
     * @param transactionId The transaction ID to fetch refunds for
     * @return List of refunds for the transaction
     */
    public List<RefundResponse> getRefundsByTransaction(String transactionId) {
        log.info("Fetching refunds for transaction ID: {}", transactionId);
        return refundRepository.findByTransactionId(transactionId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Refund entity to RefundResponse DTO
     */
    private RefundResponse convertToResponse(Refund refund) {
        return new RefundResponse(
                refund.getRefundId(),
                refund.getTransactionId(),
                refund.getAmount(),
                refund.getRefundTimestamp().toString(),
                refund.getStatus(),
                refund.getReason()
        );
    }
}
