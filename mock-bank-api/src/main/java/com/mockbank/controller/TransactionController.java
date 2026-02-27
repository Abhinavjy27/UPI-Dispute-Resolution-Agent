package com.mockbank.controller;

import com.mockbank.dto.ApiResponse;
import com.mockbank.dto.CreateTransactionRequest;
import com.mockbank.dto.TransactionResponse;
import com.mockbank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Transaction operations
 * Endpoints:
 * - POST /bank/transaction - Create a transaction
 * - GET /bank/transaction/{transactionId} - Fetch transaction details
 */
@RestController
@RequestMapping("/bank/transaction")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction Management API")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create a new transaction
     *
     * @param request CreateTransactionRequest containing transaction details
     * @return ResponseEntity with ApiResponse containing the created transaction
     */
    @PostMapping
    @Operation(summary = "Create a new transaction",
            description = "Creates a new transaction with the provided details. The transaction will be stored with SUCCESS or FAILED status.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key")
    })
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @RequestBody CreateTransactionRequest request,
            @RequestHeader(value = "x-api-key", required = true) String apiKey) {

        log.info("POST /bank/transaction - Creating transaction: {}", request.getTransactionId());
        TransactionResponse response = transactionService.createTransaction(request);

        ApiResponse<TransactionResponse> apiResponse = new ApiResponse<>(
                true,
                "Transaction created successfully",
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Fetch transaction details by transaction ID
     *
     * @param transactionId The transaction ID to fetch
     * @return ResponseEntity with ApiResponse containing transaction details
     */
    @GetMapping("/{transactionId}")
    @Operation(summary = "Fetch transaction details",
            description = "Retrieves the details of a transaction by its transaction ID. Returns 404 if transaction not found.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transaction found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key")
    })
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @Parameter(description = "Transaction ID", example = "TXN20240101001", required = true)
            @PathVariable String transactionId,
            @RequestHeader(value = "x-api-key", required = true) String apiKey) {

        log.info("GET /bank/transaction/{} - Fetching transaction details", transactionId);
        TransactionResponse response = transactionService.getTransaction(transactionId);

        ApiResponse<TransactionResponse> apiResponse = new ApiResponse<>(
                true,
                "Transaction retrieved successfully",
                response
        );

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
