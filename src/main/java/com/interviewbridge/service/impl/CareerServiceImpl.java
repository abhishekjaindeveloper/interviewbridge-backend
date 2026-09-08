package com.interviewbridge.service.impl;

import com.interviewbridge.dto.response.JobResponse;
import com.interviewbridge.service.CareerService;
import com.interviewbridge.service.JobProviderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerServiceImpl implements CareerService {

    private final JobProviderFactory jobProviderFactory;

    @Override
    public List<JobResponse> searchJobs(String provider, String query) {
        String providerName = (provider != null && !provider.trim().isEmpty()) ? provider.trim() : "REMOTEOK";
        return jobProviderFactory.getProvider(providerName).fetchJobs(query);
    }
}
