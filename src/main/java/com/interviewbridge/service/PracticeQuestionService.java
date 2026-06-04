package com.interviewbridge.service;

import com.interviewbridge.response.PracticeQuestionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface defining contracts for PracticeQuestion management.
 */
public interface PracticeQuestionService {

    /**
     * Generates questions for the specified practice session and saves them.
     *
     * @param email     the authenticated user's email
     * @param sessionId the practice session UUID
     * @return the list of created practice question responses
     */
    List<PracticeQuestionResponse> generateAndSaveQuestions(String email, UUID sessionId);

    /**
     * Retrieves all questions associated with a practice session.
     *
     * @param email     the authenticated user's email
     * @param sessionId the practice session UUID
     * @return the list of practice question responses
     */
    List<PracticeQuestionResponse> getQuestionsForSession(String email, UUID sessionId);

    /**
     * Retrieves a specific question by session ID and question number.
     *
     * @param email          the authenticated user's email
     * @param sessionId      the practice session UUID
     * @param questionNumber the question number (e.g. 1 to 10)
     * @return the practice question details
     */
    PracticeQuestionResponse getQuestionByNumber(String email, UUID sessionId, Integer questionNumber);
}
