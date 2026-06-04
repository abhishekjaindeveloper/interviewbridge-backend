package com.interviewbridge.service;

import com.interviewbridge.request.AIEvaluationRequest;
import com.interviewbridge.response.AIEvaluationResponse;

/**
 * Service interface for communicating with the AI evaluation provider.
 */
public interface AIEvaluationService {

    /**
     * Sends a request to the AI provider to evaluate a question response.
     *
     * @param request the AI evaluation request details
     * @return the AI evaluation response containing mock or real AI results
     */
    AIEvaluationResponse evaluateAnswer(AIEvaluationRequest request);
}
