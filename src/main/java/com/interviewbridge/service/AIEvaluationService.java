package com.interviewbridge.service;

import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;

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

