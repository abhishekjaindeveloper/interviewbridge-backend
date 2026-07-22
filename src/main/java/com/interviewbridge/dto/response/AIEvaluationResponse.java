package com.interviewbridge.dto.response;

/**
 * Response payload returned from the AI evaluation engine.
 */
public record AIEvaluationResponse(
    String translatedAnswer,
    String improvedAnswer,
    String explanation,
    Integer score
) {}
