package com.interviewbridge.dto.response;

import com.interviewbridge.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing details of a practice session returned to clients.
 */
public record PracticeSessionResponse(
    UUID id,
    UUID userId,
    String userName,
    UUID technologyId,
    String technologyName,
    UUID experienceId,
    String experienceLabel,
    SessionStatus sessionStatus,
    Integer totalQuestions,
    Integer completedQuestions,
    Double averageScore,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

