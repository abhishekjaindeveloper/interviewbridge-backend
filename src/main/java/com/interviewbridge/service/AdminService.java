package com.interviewbridge.service;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.response.AdminUserResponse;
import com.interviewbridge.response.AdminUserStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param id     the user UUID to reject
     * @param reason the reason for rejection
     */
    void rejectUser(UUID id, String reason);

    /**
     * Retrieves a paginated, filtered, and searched list of users.
     */
    Page<AdminUserResponse> getUsers(Pageable pageable, ApprovalStatus approvalStatus, Boolean isActive, String search);

    /**
     * Activates a user account.
     */
    void activateUser(UUID id);

    /**
     * Deactivates a user account.
     */
    void deactivateUser(UUID id);

    /**
     * Retrieves user account counts by status and active state.
     */
    AdminUserStatisticsResponse getUserStatistics();
}
