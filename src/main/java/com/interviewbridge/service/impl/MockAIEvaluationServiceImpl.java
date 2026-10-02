package com.interviewbridge.service.impl;

import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;
import com.interviewbridge.service.AIEvaluationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of AIEvaluationService for testing and mock profile environments.
 */
@Service
@Profile("mock")
public class MockAIEvaluationServiceImpl implements AIEvaluationService {

    @Override
    public AIEvaluationResponse evaluateAnswer(AIEvaluationRequest request) {
        String mockTranslated = "[Mock Translation] " + (request.userAnswer() != null ? request.userAnswer() : "");
        String mockImproved = "[Mock Improved Answer] Here is an improved version for " + request.technology() + ": " +
                "A more structured and precise answer explaining key concepts professionally.";
        String mockExplanation = "[Mock Explanation] The answer is good but could be more detailed. " +
                "Key points covered: Concept definition. Missing elements: real-world examples.";
        Integer mockScore = 8;
        String mockWhatWasCorrect = "[Mock Correct] Accurately defined the core terminology and stated the primary use case.";
        String mockWhatWasMissing = "[Mock Missing] Could elaborate more on edge-case handling and performance implications.";

        return new AIEvaluationResponse(
            mockTranslated,
            mockImproved,
            mockExplanation,
            mockScore,
            mockWhatWasCorrect,
            mockWhatWasMissing
        );
    }
}
