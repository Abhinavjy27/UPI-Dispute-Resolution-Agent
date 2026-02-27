package com.mockbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    private String transactionId;
    private Double amount;
    private String status; // SUCCESS, FAILED
    private String payerId;
    private String payeeId;
    private String description;
}
