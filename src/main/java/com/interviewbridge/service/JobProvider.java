package com.interviewbridge.service;

import com.interviewbridge.dto.response.JobResponse;
import java.util.List;

public interface JobProvider {
    List<JobResponse> fetchJobs(String query);
    String getProviderName();
}
