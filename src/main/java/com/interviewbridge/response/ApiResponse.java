package com.interviewbridge.response;

/**
 * Standard generic record representing a successful API response envelope.
 */
public record ApiResponse<T>(
    boolean success,
    String message,
    T data
) {}
