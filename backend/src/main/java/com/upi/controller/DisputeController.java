package com.upi.controller;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.service.DisputeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DisputeController {
    private static final Logger logger = Logger.getLogger(DisputeController.class.getName());
    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping("/disputes")
    public ResponseEntity<DisputeResponse> fileDispute(@Valid @RequestBody DisputeRequest request) {
        try {
            logger.info("POST /api/disputes - Filing new dispute");
            DisputeResponse response = disputeService.fileDispute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.severe("Validation error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Error filing dispute: " + e.getMessage());
            throw new RuntimeException("Failed to file dispute: " + e.getMessage());
        }
    }

    @GetMapping("/disputes/{id}")
    public ResponseEntity<DisputeResponse> getDisputeStatus(@PathVariable Long id) {
        try {
            logger.info("GET /api/disputes/" + id + " - Fetching status");
            DisputeResponse response = disputeService.getDisputeStatus(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.severe("Dispute not found: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "UPI Dispute Resolution API");
        return ResponseEntity.ok(response);
    }
}
