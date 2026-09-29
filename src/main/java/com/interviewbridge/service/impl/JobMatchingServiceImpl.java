package com.interviewbridge.service.impl;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.response.MatchedJobResponse;
import com.interviewbridge.entity.Job;
import com.interviewbridge.entity.User;
import com.interviewbridge.enums.WorkMode;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.JobRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.service.JobMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobMatchingServiceImpl implements JobMatchingService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MatchedJobResponse> getMatchedJobsForUser(String userEmail) {
        String normalizedEmail = userEmail != null ? userEmail.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));

        List<Job> activeJobs = jobRepository.findAll().stream()
            .filter(Job::getIsActive)
            .filter(j -> "ACTIVE".equalsIgnoreCase(j.getStatus()))
            .toList();

        List<MatchedJobResponse> matchedJobs = new ArrayList<>();

        for (Job job : activeJobs) {
            int score = calculateScore(user, job);
            matchedJobs.add(mapToMatchedJobResponse(job, score));
        }

        return matchedJobs.stream()
            .sorted((j1, j2) -> Integer.compare(j2.matchScore(), j1.matchScore()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MatchedJobResponse getJobByIdForUser(UUID id, String userEmail) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_JOB_NOT_FOUND + id));

        if (!Boolean.TRUE.equals(job.getIsActive()) || !"ACTIVE".equalsIgnoreCase(job.getStatus())) {
            throw new ResourceNotFoundException(SecurityConstants.MSG_JOB_NOT_FOUND + id);
        }

        int score = 0;
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            String normalizedEmail = userEmail.trim().toLowerCase();
            Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
            if (userOpt.isPresent()) {
                score = calculateScore(userOpt.get(), job);
            }
        }

        return mapToMatchedJobResponse(job, score);
    }

    private int calculateScore(User user, Job job) {
        int score = 0;

        // 1. Technology Match (50 points)
        if (user.getTechnology() != null) {
            String tech = user.getTechnology().getTechnologyName().toLowerCase().trim();
            String title = job.getTitle() != null ? job.getTitle().toLowerCase() : "";
            String desc = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
            String tags = job.getTags() != null ? job.getTags().toLowerCase() : "";

            if (title.contains(tech) || desc.contains(tech) || tags.contains(tech)) {
                score += 50;
            }
        }

        // 2. Location Match (20 points)
        if (user.getPreferredLocation() != null && !user.getPreferredLocation().trim().isEmpty()) {
            String preferredLoc = user.getPreferredLocation().toLowerCase().trim();
            String jobLoc = job.getLocation() != null ? job.getLocation().toLowerCase().trim() : "";

            if (jobLoc.contains(preferredLoc) || preferredLoc.contains(jobLoc) ||
                (preferredLoc.contains("remote") && "REMOTE".equalsIgnoreCase(jobLoc)) ||
                (jobLoc.contains("remote") && "REMOTE".equalsIgnoreCase(preferredLoc))) {
                score += 20;
            }
        }

        // 3. Work Mode Match (20 points)
        if (user.getPreferredWorkMode() != null && job.getWorkMode() != null) {
            if (user.getPreferredWorkMode() == job.getWorkMode()) {
                score += 20;
            }
        }

        // 4. Experience Match (10 points)
        if (user.getExperience() != null) {
            String exp = user.getExperience().getExperienceLabel().toLowerCase().trim();
            String title = job.getTitle() != null ? job.getTitle().toLowerCase() : "";
            String desc = job.getDescription() != null ? job.getDescription().toLowerCase() : "";

            if (title.contains(exp) || desc.contains(exp)) {
                score += 10;
            }
        }

        return score;
    }

    private MatchedJobResponse mapToMatchedJobResponse(Job job, int score) {
        return new MatchedJobResponse(
            job.getId(),
            job.getProvider(),
            job.getExternalJobId(),
            job.getCompany(),
            job.getTitle(),
            job.getDescription(),
            job.getLocation(),
            job.getSalaryMin(),
            job.getSalaryMax(),
            job.getEmploymentType(),
            job.getWorkMode(),
            job.getApplyUrl(),
            job.getCompanyLogo(),
            job.getTags(),
            job.getPostedAt(),
            score
        );
    }
}
