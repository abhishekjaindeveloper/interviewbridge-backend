package com.interviewbridge.repository;

import com.interviewbridge.entity.TechnologyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing TechnologyMaster database operations.
 */
@Repository
public interface TechnologyMasterRepository extends JpaRepository<TechnologyMaster, UUID> {

    /**
     * Checks if a technology already exists with the given name (case-insensitive).
     *
     * @param technologyName the technology name to check
     * @return true if the name already exists
     */
    boolean existsByTechnologyNameIgnoreCase(String technologyName);

    /**
     * Checks if another technology exists with the given name excluding a specific ID (case-insensitive).
     * Used during update operations.
     *
     * @param technologyName the technology name to check
     * @param id the ID to exclude from search
     * @return true if another technology already uses the name
     */
    boolean existsByTechnologyNameIgnoreCaseAndIdNot(String technologyName, UUID id);

    /**
     * Finds all technologies that are currently active.
     *
     * @return a list of active technologies
     */
    List<TechnologyMaster> findByIsActiveTrue();
}
