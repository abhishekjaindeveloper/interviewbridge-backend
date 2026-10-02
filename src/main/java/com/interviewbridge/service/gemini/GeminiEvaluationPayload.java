package com.interviewbridge.service.gemini;

/**
 * Payload representing the structured JSON evaluation response returned by the Gemini AI model.
 */
public record GeminiEvaluationPayload(
    String translatedAnswer,
    String improvedAnswer,
    String explanation,
    Integer score,
    String whatWasCorrect,
    String whatWasMissing
) {}
