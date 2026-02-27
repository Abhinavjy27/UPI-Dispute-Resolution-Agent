package com.mockbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initiating a refund
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    private String transactionId;
    private String reason;
}
