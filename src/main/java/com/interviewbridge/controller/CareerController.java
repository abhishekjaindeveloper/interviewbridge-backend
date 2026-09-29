package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.dto.response.JobResponse;
import com.interviewbridge.dto.response.JobSyncSummary;
import com.interviewbridge.dto.response.MatchedJobResponse;
import com.interviewbridge.service.CareerService;
import com.interviewbridge.service.JobMatchingService;
import com.interviewbridge.service.JobSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Controller exposing CareerPilot Job REST APIs for live searching, ADMIN job synchronization,
 * user job matching, and single job details.
 */
@RestController
@RequiredArgsConstructor
public class CareerController {

    private final CareerService careerService;
    private final JobSyncService jobSyncService;
    private final JobMatchingService jobMatchingService;

    /**
     * Endpoint to manually trigger job synchronization from a provider (Admin Only).
     *
     * @param provider external provider name (defaults to REMOTEOK)
     * @return summary of synced jobs
     */
    @PostMapping(SecurityConstants.ADMIN_JOB_SYNC_URL)
    @PreAuthorize(SecurityConstants.ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<JobSyncSummary>> syncJobs(
        @RequestParam(value = "provider", required = false, defaultValue = "REMOTEOK") String provider
    ) {
        JobSyncSummary summary = jobSyncService.syncJobs(provider);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_JOB_SYNC_SUCCESS,
            summary
        ));
    }

    /**
     * Endpoint for live job search from external provider (Authenticated Users).
     *
     * @param provider external provider name (defaults to REMOTEOK)
     * @param query    optional keyword filter
     * @return list of live jobs from provider
     */
    @GetMapping(SecurityConstants.USER_JOBS_BASE_URL)
    @PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<List<JobResponse>>> getJobs(
        @RequestParam(value = "provider", required = false, defaultValue = "REMOTEOK") String provider,
        @RequestParam(value = "query", required = false) String query
    ) {
        List<JobResponse> jobs = careerService.searchJobs(provider, query);
        return ResponseEntity.ok(new ApiResponse<>(true, "Jobs fetched successfully", jobs));
    }

    /**
     * Endpoint to retrieve jobs matched to authenticated user's preferences (Authenticated Users).
     *
     * @param principal authenticated user security context
     * @return list of matched jobs with match scores
     */
    @GetMapping(SecurityConstants.USER_MATCHED_JOBS_URL)
    @PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<List<MatchedJobResponse>>> getMatchedJobs(
        Principal principal
    ) {
        String email = principal.getName();
        List<MatchedJobResponse> matchedJobs = jobMatchingService.getMatchedJobsForUser(email);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_MATCHED_JOBS_SUCCESS,
            matchedJobs
        ));
    }

    /**
     * Endpoint to retrieve single job details by ID (Authenticated Users).
     *
     * @param id        the job UUID
     * @param principal authenticated user security context
     * @return job details with match score
     */
    @GetMapping(SecurityConstants.USER_JOB_DETAILS_URL)
    @PreAuthorize(SecurityConstants.USER_OR_ADMIN_ACCESS)
    public ResponseEntity<ApiResponse<MatchedJobResponse>> getJobById(
        @PathVariable(name = SecurityConstants.PATH_VAR_ID) UUID id,
        Principal principal
    ) {
        String email = principal != null ? principal.getName() : null;
        MatchedJobResponse jobDetails = jobMatchingService.getJobByIdForUser(id, email);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_JOB_DETAILS_SUCCESS,
            jobDetails
        ));
    }
}
