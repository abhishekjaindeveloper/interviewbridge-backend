package com.interviewbridge.service;

import com.interviewbridge.request.AIQuestionRequest;
import com.interviewbridge.response.AIQuestionResponse;

import java.util.List;

/**
 * Service interface for communicating with the AI model to generate interview questions.
 */
public interface AIQuestionGenerationService {

    /**
     * Generates a list of questions tailored to the specified technology and experience level.
     *
     * @param request the question details
     * @return the list of generated questions
     */
    List<AIQuestionResponse> generateQuestions(AIQuestionRequest request);
}
