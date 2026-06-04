package com.interviewbridge.response;

import java.time.LocalDateTime;

/**
 * Standard record representing an error or exception API response envelope.
 */
public record ErrorResponse(
    boolean success,
    String message,
    LocalDateTime timestamp
) {}
