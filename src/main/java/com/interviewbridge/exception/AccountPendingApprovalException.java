package com.interviewbridge.exception;

/**
 * Exception thrown when a user attempts to log in but their account approval status is pending.
 */
public class AccountPendingApprovalException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public AccountPendingApprovalException(String message) {
        super(message);
    }
}
