package com.interviewbridge.dto.response;

import com.interviewbridge.enums.ProfileStatus;
import com.interviewbridge.enums.Role;
import com.interviewbridge.enums.WorkMode;
import java.util.UUID;

/**
 * DTO representing the user profile response envelope.
 */
public record UserProfileResponse(
    UUID userId,
    String name,
    String email,
    String phoneNumber,
    Role role,
    TechnologyResponse technology,
    ExperienceResponse experience,
    String preferredJobRole,
    String preferredLocation,
    WorkMode preferredWorkMode,
    Double expectedSalary,
    Boolean jobAlertEnabled,
    ProfileStatus profileStatus
) {}

