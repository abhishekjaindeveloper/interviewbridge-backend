package com.interviewbridge.exception;

/**
 * Exception thrown when an operation cannot be performed due to invalid state.
 */
public class InvalidOperationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidOperationException(String message) {
        super(message);
    }
}
