package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.dto.response.PracticeQuestionResponse;
import com.interviewbridge.service.PracticeQuestionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing Practice Question REST APIs.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
public class PracticeQuestionController {

    private final PracticeQuestionService practiceQuestionService;

    /**
     * Endpoint to generate questions for a specific practice session.
     *
     * @param sessionId the session UUID
     * @param principal the authenticated user context
     * @return response entity indicating question generation success
     */
    @PostMapping(SecurityConstants.USER_SESSION_QUESTIONS_GENERATE_URL)
    public ResponseEntity<ApiResponse<List<PracticeQuestionResponse>>> generateQuestions(
        @PathVariable(name = SecurityConstants.PATH_VAR_SESSION_ID) UUID sessionId,
        Principal principal
    ) {
        String email = principal.getName();
        List<PracticeQuestionResponse> response = practiceQuestionService.generateAndSaveQuestions(email, sessionId);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_QUESTIONS_GENERATED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint to retrieve all questions associated with a practice session.
     *
     * @param sessionId the session UUID
     * @param principal the authenticated user context
     * @return response entity containing practice questions list
     */
    @GetMapping(SecurityConstants.USER_SESSION_QUESTIONS_URL)
    public ResponseEntity<ApiResponse<List<PracticeQuestionResponse>>> getQuestionsForSession(
        @PathVariable(name = SecurityConstants.PATH_VAR_SESSION_ID) UUID sessionId,
        Principal principal
    ) {
        String email = principal.getName();
        List<PracticeQuestionResponse> response = practiceQuestionService.getQuestionsForSession(email, sessionId);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to retrieve a specific question by session ID and question number.
     *
     * @param sessionId      the session UUID
     * @param questionNumber the question number (e.g. 1 to 10)
     * @param principal      the authenticated user context
     * @return response entity containing question details
     */
    @GetMapping(SecurityConstants.USER_SESSION_QUESTIONS_NUM_URL)
    public ResponseEntity<ApiResponse<PracticeQuestionResponse>> getQuestionByNumber(
        @PathVariable(name = SecurityConstants.PATH_VAR_SESSION_ID) UUID sessionId,
        @PathVariable(name = SecurityConstants.PATH_VAR_QUESTION_NUMBER) Integer questionNumber,
        Principal principal
    ) {
        String email = principal.getName();
        PracticeQuestionResponse response = practiceQuestionService.getQuestionByNumber(email, sessionId, questionNumber);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}

