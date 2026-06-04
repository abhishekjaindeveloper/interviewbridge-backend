package com.interviewbridge.exception;

/**
 * Exception thrown when a resource being created already exists (e.g. duplicate email).
 */
public class DuplicateResourceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) {
        super(message);
    }
}
