package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.response.AdminUserResponse;
import com.interviewbridge.response.ApiResponse;
import com.interviewbridge.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

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
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id
    ) {
        adminService.rejectUser(id);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_USER_REJECTED_SUCCESS,
            null
        ));
    }
}
