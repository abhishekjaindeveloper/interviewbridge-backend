package com.interviewbridge.exception;

/**
 * Exception thrown when a user registration has been rejected and they attempt to authenticate.
 */
public class UserAccountRejectedException extends RuntimeException {
    private final String rejectionReason;

    public UserAccountRejectedException(String message, String rejectionReason) {
        super(message);
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
