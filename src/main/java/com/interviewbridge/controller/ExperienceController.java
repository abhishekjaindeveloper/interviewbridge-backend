package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.request.ExperienceCreateRequest;
import com.interviewbridge.request.ExperienceUpdateRequest;
import com.interviewbridge.response.ApiResponse;
import com.interviewbridge.response.ExperienceResponse;
import com.interviewbridge.service.ExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller exposing Experience Master REST APIs.
 */
@RestController
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    /**
     * Endpoint for creating a new experience level (Admin Only).
     *
     * @param request the creation details
     * @return response entity containing created experience
     */
    @PostMapping(SecurityConstants.ADMIN_EXP_BASE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<ExperienceResponse>> createExperience(
        @Valid @RequestBody ExperienceCreateRequest request
    ) {
        ExperienceResponse response = experienceService.createExperience(request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_EXP_CREATED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint for updating an existing experience level (Admin Only).
     *
     * @param id the experience UUID
     * @param request the update details
     * @return response entity containing updated experience
     */
    @PutMapping(SecurityConstants.ADMIN_EXP_ID_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<ExperienceResponse>> updateExperience(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id,
        @Valid @RequestBody ExperienceUpdateRequest request
    ) {
        ExperienceResponse response = experienceService.updateExperience(id, request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_EXP_UPDATED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint for retrieving all experiences (Admin Only).
     *
     * @return response entity containing list of all experiences
     */
    @GetMapping(SecurityConstants.ADMIN_EXP_BASE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getAllExperiences() {
        List<ExperienceResponse> response = experienceService.getAllExperiences();
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to retrieve experience details by ID (Admin Only).
     *
     * @param id the experience UUID
     * @return response entity containing experience details
     */
    @GetMapping(SecurityConstants.ADMIN_EXP_ID_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<ExperienceResponse>> getExperienceById(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        ExperienceResponse response = experienceService.getExperienceById(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to activate an experience level (Admin Only).
     *
     * @param id the experience UUID
     * @return success response
     */
    @PatchMapping(SecurityConstants.ADMIN_EXP_ACTIVATE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<Void>> activateExperience(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        experienceService.activateExperience(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_EXP_ACTIVATED_SUCCESS,
            null
        ));
    }

    /**
     * Endpoint to deactivate an experience level (Admin Only).
     *
     * @param id the experience UUID
     * @return success response
     */
    @PatchMapping(SecurityConstants.ADMIN_EXP_DEACTIVATE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<Void>> deactivateExperience(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        experienceService.deactivateExperience(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_EXP_DEACTIVATED_SUCCESS,
            null
        ));
    }

    /**
     * Endpoint to retrieve active experiences (All Authenticated Users).
     *
     * @return response entity containing list of active experiences
     */
    @GetMapping(SecurityConstants.USER_EXP_BASE_URL)
    @PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> getActiveExperiences() {
        List<ExperienceResponse> response = experienceService.getActiveExperiences();
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}
