package com.interviewbridge.service;

import com.interviewbridge.dto.response.JobResponse;
import java.util.List;

public interface CareerService {
    List<JobResponse> searchJobs(String provider, String query);
}
