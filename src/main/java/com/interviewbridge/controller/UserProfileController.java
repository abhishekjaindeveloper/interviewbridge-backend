package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.UserProfileSetupRequest;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.dto.response.UserProfileResponse;
import com.interviewbridge.dto.response.UserStatusResponse;
import com.interviewbridge.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Controller exposing User Profile & Selection REST APIs.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Endpoint to configure a user's selected technology and experience (profile setup).
     *
     * @param request the profile configuration details
     * @param principal the authenticated user context
     * @return response entity indicating setup success
     */
    @PostMapping(SecurityConstants.USER_PROFILE_SETUP_URL)
    public ResponseEntity<ApiResponse<UserProfileResponse>> setupProfile(
        @Valid @RequestBody UserProfileSetupRequest request,
        Principal principal
    ) {
        String email = principal.getName();
        UserProfileResponse response = userProfileService.setupProfile(email, request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_PROFILE_SETUP_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint to update a user's selected technology and experience.
     *
     * @param request the updated profile configuration details
     * @param principal the authenticated user context
     * @return response entity indicating update success
     */
    @PutMapping(SecurityConstants.USER_PROFILE_URL)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
        @Valid @RequestBody UserProfileSetupRequest request,
        Principal principal
    ) {
        String email = principal.getName();
        UserProfileResponse response = userProfileService.updateProfile(email, request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_PROFILE_UPDATED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint to retrieve the logged-in user's profile details.
     *
     * @param principal the authenticated user context
     * @return response entity containing profile details
     */
    @GetMapping(SecurityConstants.USER_PROFILE_URL)
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
        Principal principal
    ) {
        String email = principal.getName();
        UserProfileResponse response = userProfileService.getProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to validate user token/status and retrieve status details.
     *
     * @param principal the authenticated user context
     * @return response entity containing user status details
     */
    @GetMapping(SecurityConstants.USER_ME_URL)
    public ResponseEntity<ApiResponse<UserStatusResponse>> getMe(
        Principal principal
    ) {
        String email = principal.getName();
        UserStatusResponse response = userProfileService.getUserStatus(email);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}

