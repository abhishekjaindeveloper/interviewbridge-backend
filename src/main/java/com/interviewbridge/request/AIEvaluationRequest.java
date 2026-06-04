package com.interviewbridge.request;

/**
 * Request payload sent to the AI evaluation engine.
 */
public record AIEvaluationRequest(
    String question,
    String userAnswer,
    String technology,
    String experience
) {}
