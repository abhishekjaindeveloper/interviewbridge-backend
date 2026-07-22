package com.interviewbridge.service;

import com.interviewbridge.dto.request.SubmitAnswerRequest;
import com.interviewbridge.dto.response.PracticeQuestionResponse;
import com.interviewbridge.dto.response.SubmitAnswerResponse;

import java.util.UUID;

/**
 * Service interface for handling practice answer submissions and retrieval.
 */
public interface PracticeAnswerService {

    /**
     * Submits an answer for a specific practice question.
     *
     * @param email      the email of the authenticated user
     * @param questionId the UUID of the practice question
     * @param request    the answer submission request containing the answer text
     * @return the submit answer response details
     */
    SubmitAnswerResponse submitAnswer(String email, UUID questionId, SubmitAnswerRequest request);

    /**
     * Retrieves details of a specific practice question.
     *
     * @param email      the email of the authenticated user
     * @param questionId the UUID of the practice question
     * @return the practice question details
     */
    PracticeQuestionResponse getQuestionDetails(String email, UUID questionId);
}

