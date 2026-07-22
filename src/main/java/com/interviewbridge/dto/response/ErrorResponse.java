package com.interviewbridge.dto.response;

import java.time.LocalDateTime;

/**
 * Standard record representing an error or exception API response envelope.
 */
public record ErrorResponse(
    boolean success,
    String message,
    LocalDateTime timestamp,
    String rejectionReason
) {
    public ErrorResponse(boolean success, String message, LocalDateTime timestamp) {
        this(success, message, timestamp, null);
    }
}
