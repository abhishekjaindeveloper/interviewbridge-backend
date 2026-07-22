package com.interviewbridge.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for technology responses.
 */
public record TechnologyResponse(
    UUID id,
    String technologyName,
    String description,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
