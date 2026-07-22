package com.interviewbridge.dto.response;

import com.interviewbridge.enums.EvaluationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing the evaluation result details of a practice question.
 */
public record EvaluationResultResponse(
    UUID questionId,
    UUID sessionId,
    Integer questionNumber,
    String question,
    String userAnswer,
    String translatedAnswer,
    String improvedAnswer,
    String explanation,
    Integer score,
    EvaluationStatus evaluationStatus,
    LocalDateTime evaluatedAt
) {}

