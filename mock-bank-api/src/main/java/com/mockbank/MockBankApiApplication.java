package com.mockbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for Mock Bank API
 * This application simulates a real bank infrastructure for testing the UPI Dispute Resolution Agent
 */
@SpringBootApplication
public class MockBankApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockBankApiApplication.class, args);
    }
}
