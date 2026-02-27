package com.mockbank.config;

import com.mockbank.entity.Transaction;
import com.mockbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Data Initialization Configuration
 * Seeds sample transaction data for testing purposes
 */
@Configuration
@Slf4j
public class DataInitializer {

    /**
     * Initialize sample data on application startup
     */
    @Bean
    public CommandLineRunner initializeData(TransactionRepository transactionRepository) {
        return args -> {
            log.info("Initializing sample transaction data...");

            // Sample transaction 1: SUCCESS
            Transaction txn1 = new Transaction();
            txn1.setTransactionId("TXN20240101001");
            txn1.setAmount(1000.00);
            txn1.setStatus("SUCCESS");
            txn1.setTimestamp(LocalDateTime.now().minusHours(2));
            txn1.setPayerId("CUST001");
            txn1.setPayeeId("MERCHANT001");
            txn1.setDescription("Payment for grocery shopping");
            transactionRepository.save(txn1);

            // Sample transaction 2: SUCCESS
            Transaction txn2 = new Transaction();
            txn2.setTransactionId("TXN20240101002");
            txn2.setAmount(500.50);
            txn2.setStatus("SUCCESS");
            txn2.setTimestamp(LocalDateTime.now().minusHours(1));
            txn2.setPayerId("CUST002");
            txn2.setPayeeId("MERCHANT002");
            txn2.setDescription("Bill payment");
            transactionRepository.save(txn2);

            // Sample transaction 3: FAILED
            Transaction txn3 = new Transaction();
            txn3.setTransactionId("TXN20240101003");
            txn3.setAmount(250.00);
            txn3.setStatus("FAILED");
            txn3.setTimestamp(LocalDateTime.now().minusMinutes(30));
            txn3.setPayerId("CUST003");
            txn3.setPayeeId("MERCHANT003");
            txn3.setDescription("Failed transaction - insufficient funds");
            transactionRepository.save(txn3);

            // Sample transaction 4: SUCCESS
            Transaction txn4 = new Transaction();
            txn4.setTransactionId("TXN20240101004");
            txn4.setAmount(2000.00);
            txn4.setStatus("SUCCESS");
            txn4.setTimestamp(LocalDateTime.now().minusMinutes(15));
            txn4.setPayerId("CUST001");
            txn4.setPayeeId("MERCHANT004");
            txn4.setDescription("Online shopping purchase");
            transactionRepository.save(txn4);

            // Sample transaction 5: FAILED
            Transaction txn5 = new Transaction();
            txn5.setTransactionId("TXN20240101005");
            txn5.setAmount(150.00);
            txn5.setStatus("FAILED");
            txn5.setTimestamp(LocalDateTime.now().minusMinutes(5));
            txn5.setPayerId("CUST004");
            txn5.setPayeeId("MERCHANT005");
            txn5.setDescription("Transaction timeout");
            transactionRepository.save(txn5);

            log.info("Sample data initialization completed");
        };
    }
}
