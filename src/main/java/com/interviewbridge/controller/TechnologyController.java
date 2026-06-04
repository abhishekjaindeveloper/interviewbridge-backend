package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.request.TechnologyCreateRequest;
import com.interviewbridge.request.TechnologyUpdateRequest;
import com.interviewbridge.response.ApiResponse;
import com.interviewbridge.response.TechnologyResponse;
import com.interviewbridge.service.TechnologyService;
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
 * Controller exposing Technology Master REST APIs.
 */
@RestController
@RequiredArgsConstructor
public class TechnologyController {

    private final TechnologyService technologyService;

    /**
     * Endpoint for creating a new technology (Admin Only).
     *
     * @param request the creation details
     * @return response entity containing created technology
     */
    @PostMapping(SecurityConstants.ADMIN_TECH_BASE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<TechnologyResponse>> createTechnology(
        @Valid @RequestBody TechnologyCreateRequest request
    ) {
        TechnologyResponse response = technologyService.createTechnology(request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_TECH_CREATED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint for updating an existing technology (Admin Only).
     *
     * @param id the technology UUID
     * @param request the update details
     * @return response entity containing updated technology
     */
    @PutMapping(SecurityConstants.ADMIN_TECH_ID_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<TechnologyResponse>> updateTechnology(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id,
        @Valid @RequestBody TechnologyUpdateRequest request
    ) {
        TechnologyResponse response = technologyService.updateTechnology(id, request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_TECH_UPDATED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint for retrieving all technologies (Admin Only).
     *
     * @return response entity containing list of all technologies
     */
    @GetMapping(SecurityConstants.ADMIN_TECH_BASE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<List<TechnologyResponse>>> getAllTechnologies() {
        List<TechnologyResponse> response = technologyService.getAllTechnologies();
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to activate a technology (Admin Only).
     *
     * @param id the technology UUID
     * @return success response
     */
    @PatchMapping(SecurityConstants.ADMIN_TECH_ACTIVATE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<Void>> activateTechnology(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        technologyService.activateTechnology(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_TECH_ACTIVATED_SUCCESS,
            null
        ));
    }

    /**
     * Endpoint to deactivate a technology (Admin Only).
     *
     * @param id the technology UUID
     * @return success response
     */
    @PatchMapping(SecurityConstants.ADMIN_TECH_DEACTIVATE_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<Void>> deactivateTechnology(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        technologyService.deactivateTechnology(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_TECH_DEACTIVATED_SUCCESS,
            null
        ));
    }

    /**
     * Endpoint to retrieve active technologies (All Authenticated Users).
     *
     * @return response entity containing list of active technologies
     */
    @GetMapping(SecurityConstants.USER_TECH_BASE_URL)
    @PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<List<TechnologyResponse>>> getActiveTechnologies() {
        List<TechnologyResponse> response = technologyService.getActiveTechnologies();
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }

    /**
     * Endpoint to retrieve technology details by ID (Admin Only).
     *
     * @param id the technology UUID
     * @return response entity containing technology details
     */
    @GetMapping(SecurityConstants.ADMIN_TECH_ID_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<TechnologyResponse>> getTechnologyById(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        TechnologyResponse response = technologyService.getTechnologyById(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null,
            response
        ));
    }
}
