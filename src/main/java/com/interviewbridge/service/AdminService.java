package com.interviewbridge.service;

import com.interviewbridge.response.AdminUserResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface defining admin user approval business rules.
 */
public interface AdminService {

    /**
     * Retrieves all users whose approval status is pending.
     *
     * @return a list of pending users
     */
    List<AdminUserResponse> getPendingUsers();

    /**
     * Approves a user's registration.
     *
     * @param id the user UUID to approve
     */
    void approveUser(UUID id);

    /**
     * Rejects a user's registration.
     *
     * @param id the user UUID to reject
     */
    void rejectUser(UUID id);
}
