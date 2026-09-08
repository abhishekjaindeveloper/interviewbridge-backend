package com.interviewbridge.service;

import com.interviewbridge.dto.response.JobSyncSummary;

public interface JobSyncService {
    JobSyncSummary syncJobs(String providerName);
}
