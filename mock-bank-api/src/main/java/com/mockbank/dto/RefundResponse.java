package com.mockbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for Refund details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {

    @JsonProperty("refund_id")
    private Long refundId;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("refund_timestamp")
    private String refundTimestamp;

    @JsonProperty("status")
    private String status;

    @JsonProperty("reason")
    private String reason;
}
