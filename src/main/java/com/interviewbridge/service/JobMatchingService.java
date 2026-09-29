package com.interviewbridge.service;

import com.interviewbridge.dto.response.MatchedJobResponse;
import java.util.List;
import java.util.UUID;

public interface JobMatchingService {
    List<MatchedJobResponse> getMatchedJobsForUser(String userEmail);
    MatchedJobResponse getJobByIdForUser(UUID id, String userEmail);
}
