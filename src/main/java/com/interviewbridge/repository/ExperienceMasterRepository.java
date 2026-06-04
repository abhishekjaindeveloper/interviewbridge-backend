package com.interviewbridge.repository;

import com.interviewbridge.entity.ExperienceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository for managing {@link ExperienceMaster} entities.
 */
@Repository
public interface ExperienceMasterRepository extends JpaRepository<ExperienceMaster, UUID> {

    /**
     * Checks if an experience exists with the specified label (case-insensitive).
     *
     * @param experienceLabel the label to check
     * @return true if exists, false otherwise
     */
    boolean existsByExperienceLabelIgnoreCase(String experienceLabel);

    /**
     * Checks if an experience exists with the specified label, excluding a specific ID (case-insensitive).
     *
     * @param experienceLabel the label to check
     * @param id the experience ID to exclude
     * @return true if exists, false otherwise
     */
    boolean existsByExperienceLabelIgnoreCaseAndIdNot(String experienceLabel, UUID id);

    /**
     * Retrieves all active experiences.
     *
     * @return a list of active experiences
     */
    List<ExperienceMaster> findByIsActiveTrue();
}
