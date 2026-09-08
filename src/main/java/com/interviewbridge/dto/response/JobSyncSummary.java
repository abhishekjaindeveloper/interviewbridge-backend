package com.interviewbridge.dto.response;

public record JobSyncSummary(
    int jobsReceived,
    int jobsInserted,
    int jobsUpdated,
    int jobsFailed
) {}
