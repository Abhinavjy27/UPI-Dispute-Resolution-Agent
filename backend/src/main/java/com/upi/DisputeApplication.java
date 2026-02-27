package com.upi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DisputeApplication {
    public static void main(String[] args) {
        SpringApplication.run(DisputeApplication.class, args);
    }
}
