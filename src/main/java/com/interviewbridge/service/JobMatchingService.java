package com.interviewbridge.service;

import com.interviewbridge.dto.response.MatchedJobResponse;
import java.util.List;

public interface JobMatchingService {
    List<MatchedJobResponse> getMatchedJobsForUser(String userEmail);
}
