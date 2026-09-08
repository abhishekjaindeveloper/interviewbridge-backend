package com.interviewbridge.repository;

import com.interviewbridge.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    Optional<Job> findByProviderAndExternalJobId(String provider, String externalJobId);
    List<Job> findByProvider(String provider);
}
