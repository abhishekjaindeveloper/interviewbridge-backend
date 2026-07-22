package com.interviewbridge.dto.response;

import com.interviewbridge.enums.ApprovalStatus;
import com.interviewbridge.enums.Role;

/**
 * DTO for sending authentication responses containing the JWT token and user details.
 */
public record AuthResponse(
    String token,
    String email,
    String name,
    String phoneNumber,
    Role role,
    ApprovalStatus approvalStatus
) {}

