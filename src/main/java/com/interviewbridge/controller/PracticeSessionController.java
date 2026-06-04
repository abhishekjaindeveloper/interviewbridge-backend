package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.request.StartPracticeSessionRequest;
import com.interviewbridge.response.ApiResponse;
import com.interviewbridge.response.PracticeSessionResponse;
import com.interviewbridge.service.PracticeSessionService;

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
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing Practice SessionREST APIs.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
public class PracticeSessionController {

    private final PracticeSessionService practiceSessionService;

    /**
     * Endpoint to start a new practice session.
     *
     * @param request   the session configuration details
     * @param principal the authenticated user context
     * @return response entity indicating creation success
     */
    @PostMapping(SecurityConstants.USER_PRACTICE_SESSIONS_URL)
    public ResponseEntity<ApiResponse<PracticeSessionResponse>> startSession(
        @Valid @RequestBody StartPracticeSessionRequest request,
        Principal principal
    ) {
        String email = principal.getName();
        PracticeSessionResponse response = practiceSessionService.startSession(email, request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_SESSION_STARTED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint to retrieve details of a specific practice session by its ID.
     *
     * @param id        the practice session UUID
     * @param principal the authenticated user context
     * @return response entity containing practice session details
     */
    @GetMapping(SecurityConstants.USER_PRACTICE_SESSIONS_ID_URL)
    public ResponseEntity<ApiResponse<PracticeSessionResponse>> getSessionById(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id,
        Principal principal
    ) {
        String email = principal.getName();
        PracticeSessionResponse response = practiceSessionService.getSessionById(email, id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to retrieve all practice sessions of the logged-in user.
     *
     * @param principal the authenticated user context
     * @return response entity containing list of practice sessions
     */
    @GetMapping(SecurityConstants.USER_PRACTICE_SESSIONS_URL)
    public ResponseEntity<ApiResponse<List<PracticeSessionResponse>>> getSessionsForUser(
        Principal principal
    ) {
        String email = principal.getName();
        List<PracticeSessionResponse> response = practiceSessionService.getSessionsForUser(email);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}
