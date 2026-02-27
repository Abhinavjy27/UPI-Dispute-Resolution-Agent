package com.mockbank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Transaction Entity representing a UPI transaction
 * Maps to the 'transactions' table in H2 database
 */
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED, REFUNDED

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "payer_id")
    private String payerId;

    @Column(name = "payee_id")
    private String payeeId;

    @Column(name = "description")
    private String description;
}
