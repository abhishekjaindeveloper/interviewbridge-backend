package com.interviewbridge.dto.response;

import com.interviewbridge.enums.WorkMode;
import java.time.LocalDateTime;
import java.util.UUID;

public record MatchedJobResponse(
    UUID id,
    String provider,
    String externalJobId,
    String company,
    String title,
    String description,
    String location,
    Double salaryMin,
    Double salaryMax,
    String employmentType,
    WorkMode workMode,
    String applyUrl,
    String companyLogo,
    String tags,
    LocalDateTime postedAt,
    int matchScore
) {}
