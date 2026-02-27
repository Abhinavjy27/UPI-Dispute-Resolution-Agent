package com.upi.repository;

import com.upi.model.Dispute;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Optional<Dispute> findByTransactionId(String transactionId);
    List<Dispute> findByPhone(String phone);
}
