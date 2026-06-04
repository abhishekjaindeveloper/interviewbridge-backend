package com.interviewbridge.exception;

/**
 * Exception thrown when authentication fails, credentials or tokens are invalid,
 * expired, or missing.
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
