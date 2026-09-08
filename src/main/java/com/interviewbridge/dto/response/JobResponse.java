package com.interviewbridge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record JobResponse(
    String id,
    String title,
    String company,
    String description,
    String url,
    String location,
    Double salary,
    List<String> tags,
    String companyLogoUrl,
    LocalDateTime postedAt
) {}
