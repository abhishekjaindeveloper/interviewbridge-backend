package com.interviewbridge.service.gemini;

import java.util.List;

/**
 * Payload representing the structured JSON response returned by the Gemini AI model.
 */
public record GeminiQuestionsPayload(
    List<GeminiQuestionItem> questions
) {
    /**
     * An individual question item within the generated questions list.
     */
    public record GeminiQuestionItem(
        Integer questionNumber,
        String questionText,
        String referenceAnswer
    ) {}
}
