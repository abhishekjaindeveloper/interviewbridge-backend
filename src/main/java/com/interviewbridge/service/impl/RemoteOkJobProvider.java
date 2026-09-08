package com.interviewbridge.service.impl;

import com.interviewbridge.dto.response.JobResponse;
import com.interviewbridge.service.JobProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RemoteOkJobProvider implements JobProvider {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String REMOTEOK_API_URL = "https://remoteok.com/api";

    @Override
    @SuppressWarnings("unchecked")
    public List<JobResponse> fetchJobs(String query) {
        String url = REMOTEOK_API_URL;
        if (query != null && !query.trim().isEmpty()) {
            url += "?tags=" + query.trim();
        }
        
        try {
            List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
            List<JobResponse> jobs = new ArrayList<>();
            if (response != null && response.size() > 1) {
                // The first element of RemoteOK is a legal notice/metadata banner
                for (int i = 1; i < response.size(); i++) {
                    Map<String, Object> item = response.get(i);
                    jobs.add(mapToJobResponse(item));
                }
            }
            return jobs;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String getProviderName() {
        return "REMOTEOK";
    }

    private JobResponse mapToJobResponse(Map<String, Object> map) {
        String id = map.getOrDefault("id", "").toString();
        String title = map.getOrDefault("position", "").toString();
        String company = map.getOrDefault("company", "").toString();
        String description = map.getOrDefault("description", "").toString();
        String url = map.getOrDefault("url", "").toString();
        String location = map.getOrDefault("location", "").toString();
        
        List<String> tags = new ArrayList<>();
        Object tagsObj = map.get("tags");
        if (tagsObj instanceof List) {
            for (Object t : (List<?>) tagsObj) {
                tags.add(t.toString());
            }
        }
        
        String companyLogoUrl = map.getOrDefault("logo", "").toString();
        LocalDateTime postedAt = null;
        Object dateObj = map.get("date");
        if (dateObj != null) {
            try {
                Instant instant = Instant.parse(dateObj.toString());
                postedAt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            } catch (Exception e) {
                postedAt = LocalDateTime.now();
            }
        } else {
            postedAt = LocalDateTime.now();
        }

        return new JobResponse(
            id,
            title,
            company,
            description,
            url,
            location,
            0.0,
            tags,
            companyLogoUrl,
            postedAt
        );
    }
}
