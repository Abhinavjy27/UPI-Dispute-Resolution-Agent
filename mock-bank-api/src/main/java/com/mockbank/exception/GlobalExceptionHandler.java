package com.mockbank.exception;

import com.mockbank.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global Exception Handler for all REST endpoints
 * Provides consistent error responses across the application
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle TransactionNotFoundException
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        log.error("Transaction not found: {}", ex.getMessage());
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle InvalidRefundException
     */
    @ExceptionHandler(InvalidRefundException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidRefundException(InvalidRefundException ex) {
        log.error("Invalid refund operation: {}", ex.getMessage());
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle generic RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(false, "An error occurred: " + ex.getMessage(), null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
        ApiResponse<?> response = new ApiResponse<>(false, "An unexpected error occurred", null);
        response.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
