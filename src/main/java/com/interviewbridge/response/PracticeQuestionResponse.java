package com.interviewbridge.response;

import com.interviewbridge.Enum.EvaluationStatus;
import com.interviewbridge.Enum.QuestionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing details of a practice question returned to clients.
 */
public record PracticeQuestionResponse(
    UUID id,
    UUID practiceSessionId,
    Integer questionNumber,
    String question,
    String userAnswer,
    String translatedAnswer,
    String improvedAnswer,
    String explanation,
    Integer score,
    QuestionStatus questionStatus,
    EvaluationStatus evaluationStatus,
    LocalDateTime evaluatedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
