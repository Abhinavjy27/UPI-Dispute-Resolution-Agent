package com.mockbank.repository;

import com.mockbank.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for Refund entity
 * Provides CRUD operations and custom queries for refunds
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findByTransactionId(String transactionId);
}
