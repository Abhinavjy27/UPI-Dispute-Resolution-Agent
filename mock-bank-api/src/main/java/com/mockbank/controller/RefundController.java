package com.mockbank.controller;

import com.mockbank.dto.ApiResponse;
import com.mockbank.dto.RefundRequest;
import com.mockbank.dto.RefundResponse;
import com.mockbank.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Refund operations
 * Endpoints:
 * - POST /bank/refund - Trigger a refund
 * - GET /bank/refunds - Fetch all refunds
 */
@RestController
@RequestMapping("/bank/refund")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Refunds", description = "Refund Management API")
public class RefundController {

    private final RefundService refundService;

    /**
     * Process a refund for a transaction
     * Business Logic:
     * - If transaction status is SUCCESS: Creates refund record and updates transaction to REFUNDED
     * - If transaction status is FAILED: Returns 400 with error message
     * - If transaction not found: Returns 404
     *
     * @param request RefundRequest containing transaction ID
     * @return ResponseEntity with ApiResponse containing refund details
     */
    @PostMapping
    @Operation(summary = "Process a refund",
            description = "Processes a refund for a transaction. The transaction must have SUCCESS status to be refundable.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Refund processed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid refund - Transaction is FAILED or already REFUNDED"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transaction not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key")
    })
    public ResponseEntity<ApiResponse<RefundResponse>> processRefund(
            @RequestBody RefundRequest request,
            @RequestHeader(value = "x-api-key", required = true) String apiKey) {

        log.info("POST /bank/refund - Processing refund for transaction: {}", request.getTransactionId());
        RefundResponse response = refundService.processRefund(request);

        ApiResponse<RefundResponse> apiResponse = new ApiResponse<>(
                true,
                "Refund processed successfully",
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Fetch all refunds
     *
     * @return ResponseEntity with ApiResponse containing list of all refunds
     */
    @GetMapping("/all")
    @Operation(summary = "Fetch all refunds",
            description = "Retrieves the list of all refunds processed by the bank.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Refunds retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid API key")
    })
    public ResponseEntity<ApiResponse<List<RefundResponse>>> getAllRefunds(
            @RequestHeader(value = "x-api-key", required = true) String apiKey) {

        log.info("GET /bank/refunds - Fetching all refunds");
        List<RefundResponse> refunds = refundService.getAllRefunds();

        ApiResponse<List<RefundResponse>> apiResponse = new ApiResponse<>(
                true,
                "Refunds retrieved successfully",
                refunds
        );

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
