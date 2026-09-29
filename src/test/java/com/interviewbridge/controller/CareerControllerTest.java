package com.interviewbridge.controller;

import com.interviewbridge.dto.response.JobSyncSummary;
import com.interviewbridge.dto.response.MatchedJobResponse;
import com.interviewbridge.enums.WorkMode;
import com.interviewbridge.service.CareerService;
import com.interviewbridge.service.JobMatchingService;
import com.interviewbridge.service.JobSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareerControllerTest {

    @Mock
    private CareerService careerService;

    @Mock
    private JobSyncService jobSyncService;

    @Mock
    private JobMatchingService jobMatchingService;

    @Mock
    private Principal principal;

    @InjectMocks
    private CareerController careerController;

    private UUID jobId;
    private MatchedJobResponse sampleMatchedJob;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
        sampleMatchedJob = new MatchedJobResponse(
            jobId,
            "REMOTEOK",
            "123",
            "TechCorp",
            "Java Developer",
            "Java Job",
            "Remote",
            100000.0,
            150000.0,
            "FULL_TIME",
            WorkMode.REMOTE,
            "https://example.com",
            "https://logo.png",
            "java,spring",
            LocalDateTime.now(),
            90
        );
    }

    @Test
    void syncJobs_Success() {
        JobSyncSummary summary = new JobSyncSummary(10, 8, 2, 0);
        when(jobSyncService.syncJobs("REMOTEOK")).thenReturn(summary);

        ResponseEntity<?> response = careerController.syncJobs("REMOTEOK");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(jobSyncService, times(1)).syncJobs("REMOTEOK");
    }

    @Test
    void getMatchedJobs_Success() {
        when(principal.getName()).thenReturn("user@example.com");
        when(jobMatchingService.getMatchedJobsForUser("user@example.com")).thenReturn(List.of(sampleMatchedJob));

        ResponseEntity<?> response = careerController.getMatchedJobs(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(jobMatchingService, times(1)).getMatchedJobsForUser("user@example.com");
    }

    @Test
    void getJobById_Success() {
        when(principal.getName()).thenReturn("user@example.com");
        when(jobMatchingService.getJobByIdForUser(jobId, "user@example.com")).thenReturn(sampleMatchedJob);

        ResponseEntity<?> response = careerController.getJobById(jobId, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(jobMatchingService, times(1)).getJobByIdForUser(jobId, "user@example.com");
    }
}
