package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.dto.response.EvaluationResultResponse;
import com.interviewbridge.service.PracticeEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

/**
 * Controller exposing Practice Question Evaluation REST APIs.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
public class PracticeEvaluationController {

    private final PracticeEvaluationService practiceEvaluationService;

    /**
     * Endpoint to trigger evaluation for an answered practice question.
     *
     * @param questionId the question UUID
     * @param principal  the authenticated user context
     * @return response entity indicating evaluation success and results
     */
    @PostMapping(SecurityConstants.USER_PRACTICE_QUESTIONS_EVALUATE_URL)
    public ResponseEntity<ApiResponse<EvaluationResultResponse>> evaluateQuestion(
        @PathVariable(name = SecurityConstants.PATH_VAR_QUESTION_ID) UUID questionId,
        Principal principal
    ) {
        String email = principal.getName();
        EvaluationResultResponse response = practiceEvaluationService.evaluateQuestion(email, questionId);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_EVALUATION_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint to retrieve stored evaluation results for a practice question.
     *
     * @param questionId the question UUID
     * @param principal  the authenticated user context
     * @return response entity containing evaluation results
     */
    @GetMapping(SecurityConstants.USER_PRACTICE_QUESTIONS_EVALUATION_URL)
    public ResponseEntity<ApiResponse<EvaluationResultResponse>> getEvaluationResults(
        @PathVariable(name = SecurityConstants.PATH_VAR_QUESTION_ID) UUID questionId,
        Principal principal
    ) {
        String email = principal.getName();
        EvaluationResultResponse response = practiceEvaluationService.getEvaluationResults(email, questionId);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}

