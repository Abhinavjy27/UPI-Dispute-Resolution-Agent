package com.upi.controller;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.service.DisputeService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> fileDispute(@Valid @RequestBody DisputeRequest request) {
        try {
            logger.info("POST /api/disputes - Filing new dispute");
            DisputeResponse response = disputeService.fileDispute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.severe("Validation error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            logger.severe("Error filing dispute: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to file dispute");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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

    @GetMapping("/disputes/user/{phone}")
    public ResponseEntity<?> getUserDisputes(@PathVariable String phone) {
        try {
            logger.info("GET /api/disputes/user/" + phone + " - Fetching user disputes");
            var disputes = disputeService.getUserDisputes(phone);
            return ResponseEntity.ok(disputes);
        } catch (Exception e) {
            logger.severe("Error fetching user disputes: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/disputes/user/{phone}")
    public ResponseEntity<?> deleteUserDisputes(@PathVariable String phone) {
        try {
            logger.info("DELETE /api/disputes/user/" + phone + " - Deleting user disputes");
            disputeService.deleteUserDisputes(phone);
            Map<String, String> response = new HashMap<>();
            response.put("message", "All disputes deleted for phone: " + phone);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error deleting user disputes: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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
