package com.interviewbridge.response;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;

/**
 * DTO for sending authentication responses containing the JWT token and user details.
 */
public record AuthResponse(
    String token,
    String email,
    String name,
    Role role,
    ApprovalStatus approvalStatus
) {}
