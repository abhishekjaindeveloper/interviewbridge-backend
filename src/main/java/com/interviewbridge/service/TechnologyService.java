package com.interviewbridge.service;

import com.interviewbridge.dto.request.TechnologyCreateRequest;
import com.interviewbridge.dto.request.TechnologyUpdateRequest;
import com.interviewbridge.dto.response.TechnologyResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing technology master details.
 */
public interface TechnologyService {

    /**
     * Creates a new technology domain.
     *
     * @param request the technology creation details
     * @return the created technology details
     */
    TechnologyResponse createTechnology(TechnologyCreateRequest request);

    /**
     * Updates an existing technology domain.
     *
     * @param id the technology UUID to update
     * @param request the updated technology details
     * @return the updated technology details
     */
    TechnologyResponse updateTechnology(UUID id, TechnologyUpdateRequest request);

    /**
     * Retrieves technology details by its ID.
     *
     * @param id the technology UUID
     * @return the technology details
     */
    TechnologyResponse getTechnologyById(UUID id);

    /**
     * Retrieves all technologies (including active and inactive).
     *
     * @return a list of all technologies
     */
    List<TechnologyResponse> getAllTechnologies();

    /**
     * Retrieves only active technologies.
     *
     * @return a list of active technologies
     */
    List<TechnologyResponse> getActiveTechnologies();

    /**
     * Activates a technology domain.
     *
     * @param id the technology UUID
     */
    void activateTechnology(UUID id);

    /**
     * Deactivates a technology domain.
     *
     * @param id the technology UUID
     */
    void deactivateTechnology(UUID id);
}

