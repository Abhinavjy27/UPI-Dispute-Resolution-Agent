package com.mockbank.repository;

import com.mockbank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Transaction entity
 * Provides CRUD operations and custom queries for transactions
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
