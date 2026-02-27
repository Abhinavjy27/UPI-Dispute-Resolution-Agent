package com.mockbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Transaction details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("payer_id")
    private String payerId;

    @JsonProperty("payee_id")
    private String payeeId;

    @JsonProperty("description")
    private String description;
}
