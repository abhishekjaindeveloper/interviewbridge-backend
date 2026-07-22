package com.interviewbridge.dto.response;

import com.interviewbridge.enums.ApprovalStatus;
import com.interviewbridge.enums.Role;
import java.util.UUID;

/**
 * DTO record containing user status and details for token validation and startup checks.
 */
public record UserStatusResponse(
    UUID id,
    String name,
    String email,
    String phoneNumber,
    Role role,
    ApprovalStatus approvalStatus,
    Boolean isActive
) {}

