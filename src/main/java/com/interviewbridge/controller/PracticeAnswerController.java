package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.request.SubmitAnswerRequest;
import com.interviewbridge.response.ApiResponse;
import com.interviewbridge.response.PracticeQuestionResponse;
import com.interviewbridge.response.SubmitAnswerResponse;
import com.interviewbridge.service.PracticeAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

/**
 * Controller exposing Practice Answer submission and retrieval REST APIs.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
public class PracticeAnswerController {

    private final PracticeAnswerService practiceAnswerService;

    /**
     * Endpoint to submit an answer for a specific question.
     *
     * @param questionId the question UUID
     * @param request    the answer details
     * @param principal  the authenticated user context
     * @return response entity containing the answer submission response
     */
    @PostMapping(SecurityConstants.USER_PRACTICE_QUESTIONS_ANSWER_URL)
    public ResponseEntity<ApiResponse<SubmitAnswerResponse>> submitAnswer(
        @PathVariable(name = SecurityConstants.PATH_VAR_QUESTION_ID) UUID questionId,
        @Valid @RequestBody SubmitAnswerRequest request,
        Principal principal
    ) {
        String email = principal.getName();
        SubmitAnswerResponse response = practiceAnswerService.submitAnswer(email, questionId, request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_ANSWER_SUBMITTED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint to retrieve details of a specific question.
     *
     * @param questionId the question UUID
     * @param principal  the authenticated user context
     * @return response entity containing question details
     */
    @GetMapping(SecurityConstants.USER_PRACTICE_QUESTIONS_ID_URL)
    public ResponseEntity<ApiResponse<PracticeQuestionResponse>> getQuestionDetails(
        @PathVariable(name = SecurityConstants.PATH_VAR_QUESTION_ID) UUID questionId,
        Principal principal
    ) {
        String email = principal.getName();
        PracticeQuestionResponse response = practiceAnswerService.getQuestionDetails(email, questionId);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}
