package com.interviewbridge.service;

import com.interviewbridge.dto.request.StartPracticeSessionRequest;
import com.interviewbridge.dto.response.PracticeSessionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface defining contracts for PracticeSession management.
 */
public interface PracticeSessionService {

    /**
     * Starts a new practice session for the user.
     *
     * @param email   the authenticated user's email
     * @param request the start session request details
     * @return the created session response
     */
    PracticeSessionResponse startSession(String email, StartPracticeSessionRequest request);

    /**
     * Retrieves details of a specific practice session by its ID.
     *
     * @param email the authenticated user's email
     * @param id    the practice session UUID
     * @return the practice session response details
     */
    PracticeSessionResponse getSessionById(String email, UUID id);

    /**
     * Retrieves all practice sessions belonging to the logged-in user.
     *
     * @param email the authenticated user's email
     * @return a list of user practice session responses
     */
    List<PracticeSessionResponse> getSessionsForUser(String email);
}

