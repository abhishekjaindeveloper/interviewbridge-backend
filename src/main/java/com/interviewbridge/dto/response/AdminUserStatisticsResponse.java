package com.interviewbridge.dto.response;

/**
 * DTO representing user statistics for the admin dashboard.
 */
public record AdminUserStatisticsResponse(
    long totalUsers,
    long activeUsers,
    long inactiveUsers,
    long pendingUsers
) {}
