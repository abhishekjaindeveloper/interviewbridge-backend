package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.dto.response.JobResponse;
import com.interviewbridge.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/user/jobs")
@RequiredArgsConstructor
@PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
public class CareerController {

    private final CareerService careerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getJobs(
        @RequestParam(value = "provider", required = false, defaultValue = "REMOTEOK") String provider,
        @RequestParam(value = "query", required = false) String query
    ) {
        List<JobResponse> jobs = careerService.searchJobs(provider, query);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs fetched successfully", jobs));
    }
}
