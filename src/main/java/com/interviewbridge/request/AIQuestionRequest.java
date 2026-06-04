package com.interviewbridge.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload passed to the AI question generation engine.
 */
public record AIQuestionRequest(
    @NotBlank
    String technologyName,

    @NotBlank
    String experienceLabel,

    int numberOfQuestions
) {}
