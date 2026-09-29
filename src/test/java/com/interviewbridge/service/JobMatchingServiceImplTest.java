package com.interviewbridge.service;

import com.interviewbridge.dto.response.MatchedJobResponse;
import com.interviewbridge.entity.Job;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.enums.WorkMode;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.JobRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.service.impl.JobMatchingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobMatchingServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobMatchingServiceImpl jobMatchingService;

    private User testUser;
    private Job testJob;
    private UUID jobId;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();

        TechnologyMaster tech = new TechnologyMaster();
        tech.setTechnologyName("Java");

        testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setTechnology(tech);
        testUser.setPreferredWorkMode(WorkMode.REMOTE);
        testUser.setPreferredLocation("Remote");

        testJob = new Job();
        testJob.setId(jobId);
        testJob.setProvider("REMOTEOK");
        testJob.setExternalJobId("job-123");
        testJob.setCompany("TechCorp");
        testJob.setTitle("Java Senior Backend Developer");
        testJob.setDescription("Looking for Java expert");
        testJob.setLocation("Remote");
        testJob.setWorkMode(WorkMode.REMOTE);
        testJob.setStatus("ACTIVE");
        testJob.setIsActive(true);
    }

    @Test
    void getMatchedJobsForUser_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(jobRepository.findAll()).thenReturn(List.of(testJob));

        List<MatchedJobResponse> results = jobMatchingService.getMatchedJobsForUser("user@example.com");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testJob.getId(), results.get(0).id());
        assertTrue(results.get(0).matchScore() > 0);
    }

    @Test
    void getMatchedJobsForUser_UserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            jobMatchingService.getMatchedJobsForUser("unknown@example.com")
        );
    }

    @Test
    void getJobByIdForUser_Success() {
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

        MatchedJobResponse response = jobMatchingService.getJobByIdForUser(jobId, "user@example.com");

        assertNotNull(response);
        assertEquals(jobId, response.id());
        assertEquals("TechCorp", response.company());
    }

    @Test
    void getJobByIdForUser_JobNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(jobRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            jobMatchingService.getJobByIdForUser(nonExistentId, "user@example.com")
        );
    }

    @Test
    void getJobByIdForUser_JobInactive() {
        testJob.setIsActive(false);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(testJob));

        assertThrows(ResourceNotFoundException.class, () ->
            jobMatchingService.getJobByIdForUser(jobId, "user@example.com")
        );
    }
}
