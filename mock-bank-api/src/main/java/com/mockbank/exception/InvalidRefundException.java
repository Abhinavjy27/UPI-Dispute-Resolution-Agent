package com.mockbank.exception;

/**
 * Custom exception for invalid refund operations
 */
public class InvalidRefundException extends RuntimeException {
    public InvalidRefundException(String message) {
        super(message);
    }

    public InvalidRefundException(String message, Throwable cause) {
        super(message, cause);
    }
}
