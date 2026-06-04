package com.interviewbridge.service;

import com.interviewbridge.request.ExperienceCreateRequest;
import com.interviewbridge.request.ExperienceUpdateRequest;
import com.interviewbridge.response.ExperienceResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing experience master details.
 */
public interface ExperienceService {

    /**
     * Creates a new experience level.
     *
     * @param request the experience creation details
     * @return the created experience details
     */
    ExperienceResponse createExperience(ExperienceCreateRequest request);

    /**
     * Updates an existing experience level.
     *
     * @param id the experience UUID to update
     * @param request the updated experience details
     * @return the updated experience details
     */
    ExperienceResponse updateExperience(UUID id, ExperienceUpdateRequest request);

    /**
     * Retrieves experience details by its ID.
     *
     * @param id the experience UUID
     * @return the experience details
     */
    ExperienceResponse getExperienceById(UUID id);

    /**
     * Retrieves all experiences (including active and inactive).
     *
     * @return a list of all experiences
     */
    List<ExperienceResponse> getAllExperiences();

    /**
     * Retrieves only active experiences.
     *
     * @return a list of active experiences
     */
    List<ExperienceResponse> getActiveExperiences();

    /**
     * Activates an experience level.
     *
     * @param id the experience UUID
     */
    void activateExperience(UUID id);

    /**
     * Deactivates an experience level.
     *
     * @param id the experience UUID
     */
    void deactivateExperience(UUID id);
}
