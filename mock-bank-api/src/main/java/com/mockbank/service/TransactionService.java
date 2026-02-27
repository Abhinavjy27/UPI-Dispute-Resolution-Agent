package com.mockbank.service;

import com.mockbank.dto.CreateTransactionRequest;
import com.mockbank.dto.TransactionResponse;
import com.mockbank.entity.Transaction;
import com.mockbank.exception.TransactionNotFoundException;
import com.mockbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class for Transaction operations
 * Handles business logic for creating and fetching transactions
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Create a new transaction
     * @param request CreateTransactionRequest containing transaction details
     * @return TransactionResponse with created transaction details
     */
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        // Generate unique transaction ID if not provided
        String transactionId = "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Creating transaction with ID: {}", transactionId);

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(request.getStatus());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPayerId(request.getPayerId());
        transaction.setPayeeId(request.getPayeeId());
        transaction.setDescription(request.getDescription());

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with ID: {}", savedTransaction.getTransactionId());

        return convertToResponse(savedTransaction);
    }

    /**
     * Fetch transaction details by transaction ID
     * @param transactionId The transaction ID to fetch
     * @return TransactionResponse with transaction details
     * @throws TransactionNotFoundException if transaction not found
     */
    public TransactionResponse getTransaction(String transactionId) {
        log.info("Fetching transaction with ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.warn("Transaction not found with ID: {}", transactionId);
                    return new TransactionNotFoundException("Transaction not found with ID: " + transactionId);
                });

        return convertToResponse(transaction);
    }

    /**
     * Update transaction status
     * Used internally by refund service
     * @param transactionId The transaction ID to update
     * @param newStatus The new status to set
     */
    public void updateTransactionStatus(String transactionId, String newStatus) {
        log.info("Updating transaction status for ID: {} to: {}", transactionId, newStatus);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId));

        transaction.setStatus(newStatus);
        transactionRepository.save(transaction);

        log.info("Transaction status updated successfully");
    }

    /**
     * Convert Transaction entity to TransactionResponse DTO
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getTimestamp().toString(),
                transaction.getPayerId(),
                transaction.getPayeeId(),
                transaction.getDescription()
        );
    }
}
