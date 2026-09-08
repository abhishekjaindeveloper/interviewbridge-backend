package com.interviewbridge.service.impl;

import com.interviewbridge.dto.response.JobResponse;
import com.interviewbridge.dto.response.JobSyncSummary;
import com.interviewbridge.entity.Job;
import com.interviewbridge.enums.WorkMode;
import com.interviewbridge.repository.JobRepository;
import com.interviewbridge.service.JobProvider;
import com.interviewbridge.service.JobProviderFactory;
import com.interviewbridge.service.JobSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSyncServiceImpl implements JobSyncService {

    private final JobProviderFactory jobProviderFactory;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    public JobSyncSummary syncJobs(String providerName) {
        log.info("Starting job synchronization for provider: {}", providerName);

        JobProvider provider = jobProviderFactory.getProvider(providerName);
        List<JobResponse> fetchedJobs = provider.fetchJobs(null);

        int received = fetchedJobs.size();
        int inserted = 0;
        int updated = 0;
        int failed = 0;

        for (JobResponse fetched : fetchedJobs) {
            try {
                if (fetched.id() == null || fetched.id().trim().isEmpty()) {
                    failed++;
                    continue;
                }

                Optional<Job> existingJobOpt = jobRepository.findByProviderAndExternalJobId(
                    provider.getProviderName(),
                    fetched.id()
                );

                Job job;
                if (existingJobOpt.isPresent()) {
                    job = existingJobOpt.get();
                    updated++;
                } else {
                    job = new Job();
                    job.setProvider(provider.getProviderName());
                    job.setExternalJobId(fetched.id());
                    inserted++;
                }

                job.setCompany(fetched.company());
                job.setTitle(fetched.title());
                job.setDescription(fetched.description());
                job.setLocation(fetched.location());
                job.setSalaryMin(fetched.salary());
                job.setSalaryMax(fetched.salary());
                job.setApplyUrl(fetched.url());
                job.setCompanyLogo(fetched.companyLogoUrl());

                if (fetched.tags() != null && !fetched.tags().isEmpty()) {
                    String tagsCsv = String.join(",", fetched.tags());
                    if (tagsCsv.length() > 255) {
                        tagsCsv = tagsCsv.substring(0, 255);
                    }
                    job.setTags(tagsCsv);
                } else {
                    job.setTags(null);
                }

                job.setPostedAt(fetched.postedAt());
                job.setFetchedAt(LocalDateTime.now());
                job.setStatus("ACTIVE");
                job.setWorkMode(inferWorkMode(fetched));

                jobRepository.save(job);
            } catch (Exception e) {
                log.error("Failed to sync job with external ID: {}", fetched.id(), e);
                failed++;
            }
        }

        log.info("Job synchronization completed. Received: {}, Inserted: {}, Updated: {}, Failed: {}",
            received, inserted, updated, failed);

        return new JobSyncSummary(received, inserted, updated, failed);
    }

    private WorkMode inferWorkMode(JobResponse fetched) {
        String text = (fetched.title() + " " + fetched.location() + " " + (fetched.tags() != null ? String.join(" ", fetched.tags()) : ""))
            .toLowerCase();

        if (text.contains("remote") || text.contains("telecommute") || text.contains("work from home") || text.contains("wfh")) {
            return WorkMode.REMOTE;
        } else if (text.contains("hybrid") || text.contains("flexible")) {
            return WorkMode.HYBRID;
        } else if (text.contains("onsite") || text.contains("office") || text.contains("in-office")) {
            return WorkMode.ONSITE;
        }
        return WorkMode.REMOTE;
    }
}
