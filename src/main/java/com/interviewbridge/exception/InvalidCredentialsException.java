package com.interviewbridge.exception;

/**
 * Exception thrown when login credentials do not match.
 */
public class InvalidCredentialsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
