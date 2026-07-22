package com.interviewbridge.dto.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for validating request to start a new practice session.
 */
public record StartPracticeSessionRequest(
    @NotNull(message = EntityConstants.User.MSG_TECH_ID_REQUIRED)
    UUID technologyId,

    @NotNull(message = EntityConstants.User.MSG_EXP_ID_REQUIRED)
    UUID experienceId,

    @Min(value = EntityConstants.PracticeSession.MIN_TOTAL_QUESTIONS, message = EntityConstants.PracticeSession.MSG_TOTAL_QUESTIONS_MIN)
    @Max(value = EntityConstants.PracticeSession.MAX_TOTAL_QUESTIONS, message = EntityConstants.PracticeSession.MSG_TOTAL_QUESTIONS_MAX)
    Integer totalQuestions
) {}
