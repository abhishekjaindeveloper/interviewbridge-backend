package com.interviewbridge.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing an experience master response.
 */
public record ExperienceResponse(
    UUID id,
    String experienceLabel,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
