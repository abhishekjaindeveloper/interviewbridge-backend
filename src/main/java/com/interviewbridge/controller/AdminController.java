package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.UserRejectionRequest;
import com.interviewbridge.dto.response.AdminUserResponse;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.interviewbridge.enums.ApprovalStatus;
import com.interviewbridge.dto.response.AdminUserStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing administrator user approval and pending list APIs.
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.ADMIN_ACCESS)
public class AdminController {

    private final AdminService adminService;

    /**
     * Endpoint for retrieving a list of users pending registration approval.
     *
     * @return response entity containing a list of pending users
     */
    @GetMapping(SecurityConstants.ADMIN_USERS_PENDING_URL)
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getPendingUsers() {
        List<AdminUserResponse> pendingUsers = adminService.getPendingUsers();
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            null, // No custom message required for list queries
            pendingUsers
        ));
    }

    /**
     * Endpoint to approve user registration.
     *
     * @param id the user id
     * @return response entity indicating approval success
     */
    @PutMapping(SecurityConstants.ADMIN_USERS_APPROVE_URL)
    public ResponseEntity<ApiResponse<Void>> approveUser(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        adminService.approveUser(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_USER_APPROVED_SUCCESS,
            null
        ));
    }

    /**
     * Endpoint to reject user registration.
     *
     * @param id the user id
     * @return response entity indicating rejection success
     */
    @PutMapping(SecurityConstants.ADMIN_USERS_REJECT_URL)
    public ResponseEntity<ApiResponse<Void>> rejectUser(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id,
        @Valid @RequestBody UserRejectionRequest request
    ) {
        adminService.rejectUser(id, request.reason());
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_USER_REJECTED_SUCCESS,
            null
        ));
    }

    /**
     * Endpoint to get paginated, filtered, and searched list of users.
     */
    @GetMapping(SecurityConstants.ADMIN_USERS_BASE_URL)
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getUsers(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "approvalStatus", required = false) ApprovalStatus approvalStatus,
        @RequestParam(name = "isActive", required = false) Boolean isActive,
        @RequestParam(name = "search", required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> users = adminService.getUsers(pageable, approvalStatus, isActive, search);
        return ResponseEntity.ok(new ApiResponse<>(true, null, users));
    }

    /**
     * Endpoint to search paginated users.
     */
    @GetMapping(SecurityConstants.ADMIN_USERS_SEARCH_URL)
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> searchUsers(
        @RequestParam(name = "query", required = false) String query,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @RequestParam(name = "approvalStatus", required = false) ApprovalStatus approvalStatus,
        @RequestParam(name = "isActive", required = false) Boolean isActive
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponse> users = adminService.getUsers(pageable, approvalStatus, isActive, query);
        return ResponseEntity.ok(new ApiResponse<>(true, null, users));
    }

    /**
     * Endpoint to activate user.
     */
    @PatchMapping(SecurityConstants.ADMIN_USERS_ACTIVATE_URL)
    public ResponseEntity<ApiResponse<Void>> activateUser(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        adminService.activateUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, SecurityConstants.MSG_USER_ACTIVATED_SUCCESS, null));
    }

    /**
     * Endpoint to deactivate user.
     */
    @PatchMapping(SecurityConstants.ADMIN_USERS_DEACTIVATE_URL)
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        adminService.deactivateUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, SecurityConstants.MSG_USER_DEACTIVATED_SUCCESS, null));
    }

    /**
     * Endpoint to retrieve user counts and statistics.
     */
    @GetMapping(SecurityConstants.ADMIN_USERS_STATISTICS_URL)
    public ResponseEntity<ApiResponse<AdminUserStatisticsResponse>> getUserStatistics() {
        AdminUserStatisticsResponse stats = adminService.getUserStatistics();
        return ResponseEntity.ok(new ApiResponse<>(true, null, stats));
    }
}

