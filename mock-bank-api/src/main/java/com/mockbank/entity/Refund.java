package com.mockbank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Refund Entity representing a refund transaction
 * Maps to the 'refunds' table in H2 database
 */
@Entity
@Table(name = "refunds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Long refundId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "refund_timestamp", nullable = false)
    private LocalDateTime refundTimestamp;

    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, PENDING, FAILED

    @Column(name = "reason")
    private String reason;
}
