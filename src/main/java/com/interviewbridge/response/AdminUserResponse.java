package com.interviewbridge.response;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing the user response in administrator APIs.
 */
public record AdminUserResponse(
    UUID id,
    String name,
    String email,
    String phoneNumber,
    Role role,
    ApprovalStatus approvalStatus,
    TechnologyResponse technology,
    ExperienceResponse experience,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
