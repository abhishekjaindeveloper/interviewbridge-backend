package com.interviewbridge.service.impl;

import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;
import com.interviewbridge.service.AIEvaluationService;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of AIEvaluationService for foundation stage.
 */
@Service
public class AIEvaluationServiceImpl implements AIEvaluationService {

    @Override
    public AIEvaluationResponse evaluateAnswer(AIEvaluationRequest request) {
        // Generate deterministic mock translation, improved answer, explanation, and score
        String mockTranslated = "[Mock Translation] Translated version of the user's answer: " + request.userAnswer();
        String mockImproved = "[Mock Improved Answer] Here is an improved version for " + request.technology() + ": " +
                "A more structured and precise answer explaining key concepts professionally.";
        String mockExplanation = "[Mock Explanation] The answer is good but could be more detailed. " +
                "Key points covered: Concept definition. Missing elements: real-world examples.";
        Integer mockScore = 8; // Deterministic score within the valid range [0, 10]

        return new AIEvaluationResponse(mockTranslated, mockImproved, mockExplanation, mockScore);
    }
}

