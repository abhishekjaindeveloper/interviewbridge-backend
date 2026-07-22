package com.interviewbridge.dto.response;

import com.interviewbridge.enums.QuestionStatus;
import com.interviewbridge.enums.SessionStatus;
import java.util.UUID;

/**
 * DTO representing details of a submitted answer and the updated session progress.
 */
public record SubmitAnswerResponse(
    UUID questionId,
    UUID sessionId,
    Integer questionNumber,
    String question,
    String userAnswer,
    QuestionStatus questionStatus,
    Integer completedQuestions,
    Integer totalQuestions,
    SessionStatus sessionStatus
) {}

