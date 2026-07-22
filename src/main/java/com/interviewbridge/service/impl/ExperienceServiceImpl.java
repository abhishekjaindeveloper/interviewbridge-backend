package com.interviewbridge.service.impl;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.ExperienceMasterRepository;
import com.interviewbridge.dto.request.ExperienceCreateRequest;
import com.interviewbridge.dto.request.ExperienceUpdateRequest;
import com.interviewbridge.dto.response.ExperienceResponse;
import com.interviewbridge.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing experience master details.
 */
@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceMasterRepository repository;

    @Override
    @Transactional
    public ExperienceResponse createExperience(ExperienceCreateRequest request) {
        if (repository.existsByExperienceLabelIgnoreCase(request.experienceLabel())) {
            throw new DuplicateResourceException(SecurityConstants.MSG_EXP_LABEL_EXISTS + request.experienceLabel());
        }

        ExperienceMaster experience = ExperienceMaster.builder()
            .experienceLabel(request.experienceLabel())
            .isActive(true)
            .build();

        ExperienceMaster savedExp = repository.save(experience);
        return mapToResponse(savedExp);
    }

    @Override
    @Transactional
    public ExperienceResponse updateExperience(UUID id, ExperienceUpdateRequest request) {
        ExperienceMaster experience = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + id));

        if (repository.existsByExperienceLabelIgnoreCaseAndIdNot(request.experienceLabel(), id)) {
            throw new DuplicateResourceException(SecurityConstants.MSG_EXP_LABEL_EXISTS + request.experienceLabel());
        }

        experience.setExperienceLabel(request.experienceLabel());

        ExperienceMaster updatedExp = repository.save(experience);
        return mapToResponse(updatedExp);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceResponse getExperienceById(UUID id) {
        ExperienceMaster experience = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + id));
        return mapToResponse(experience);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponse> getAllExperiences() {
        return repository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponse> getActiveExperiences() {
        return repository.findByIsActiveTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void activateExperience(UUID id) {
        ExperienceMaster experience = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + id));
        experience.setIsActive(true);
        repository.save(experience);
    }

    @Override
    @Transactional
    public void deactivateExperience(UUID id) {
        ExperienceMaster experience = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + id));
        experience.setIsActive(false);
        repository.save(experience);
    }

    private ExperienceResponse mapToResponse(ExperienceMaster exp) {
        return new ExperienceResponse(
            exp.getId(),
            exp.getExperienceLabel(),
            exp.getIsActive(),
            exp.getCreatedAt(),
            exp.getUpdatedAt()
        );
    }
}

