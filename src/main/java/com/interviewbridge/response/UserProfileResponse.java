package com.interviewbridge.response;

import com.interviewbridge.Enum.Role;
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
    ExperienceResponse experience
) {}
