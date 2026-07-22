package com.interviewbridge.service;

import com.interviewbridge.dto.response.EvaluationResultResponse;
import java.util.UUID;

/**
 * Business service interface for managing practice question evaluations.
 */
public interface PracticeEvaluationService {

    /**
     * Evaluates an answered question using the AI evaluation service and persists the result.
     *
     * @param email      the email of the authenticated user
     * @param questionId the UUID of the practice question
     * @return the evaluation results
     */
    EvaluationResultResponse evaluateQuestion(String email, UUID questionId);

    /**
     * Retrieves stored evaluation results for a specific practice question.
     *
     * @param email      the email of the authenticated user
     * @param questionId the UUID of the practice question
     * @return the stored evaluation results
     */
    EvaluationResultResponse getEvaluationResults(String email, UUID questionId);
}

