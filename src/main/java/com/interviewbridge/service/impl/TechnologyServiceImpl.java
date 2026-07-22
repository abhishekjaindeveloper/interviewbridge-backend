package com.interviewbridge.service.impl;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.TechnologyMasterRepository;
import com.interviewbridge.dto.request.TechnologyCreateRequest;
import com.interviewbridge.dto.request.TechnologyUpdateRequest;
import com.interviewbridge.dto.response.TechnologyResponse;
import com.interviewbridge.service.TechnologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing technology master details.
 */
@Service
@RequiredArgsConstructor
public class TechnologyServiceImpl implements TechnologyService {

    private final TechnologyMasterRepository repository;

    @Override
    @Transactional
    public TechnologyResponse createTechnology(TechnologyCreateRequest request) {
        if (repository.existsByTechnologyNameIgnoreCase(request.technologyName())) {
            throw new DuplicateResourceException(SecurityConstants.MSG_TECH_NAME_EXISTS + request.technologyName());
        }

        TechnologyMaster technology = TechnologyMaster.builder()
            .technologyName(request.technologyName())
            .description(request.description())
            .isActive(true)
            .build();

        TechnologyMaster savedTech = repository.save(technology);
        return mapToResponse(savedTech);
    }

    @Override
    @Transactional
    public TechnologyResponse updateTechnology(UUID id, TechnologyUpdateRequest request) {
        TechnologyMaster technology = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + id));

        if (repository.existsByTechnologyNameIgnoreCaseAndIdNot(request.technologyName(), id)) {
            throw new DuplicateResourceException(SecurityConstants.MSG_TECH_NAME_EXISTS + request.technologyName());
        }

        technology.setTechnologyName(request.technologyName());
        technology.setDescription(request.description());

        TechnologyMaster updatedTech = repository.save(technology);
        return mapToResponse(updatedTech);
    }

    @Override
    @Transactional(readOnly = true)
    public TechnologyResponse getTechnologyById(UUID id) {
        TechnologyMaster technology = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + id));
        return mapToResponse(technology);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnologyResponse> getAllTechnologies() {
        return repository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TechnologyResponse> getActiveTechnologies() {
        return repository.findByIsActiveTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void activateTechnology(UUID id) {
        TechnologyMaster technology = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + id));
        technology.setIsActive(true);
        repository.save(technology);
    }

    @Override
    @Transactional
    public void deactivateTechnology(UUID id) {
        TechnologyMaster technology = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + id));
        technology.setIsActive(false);
        repository.save(technology);
    }

    private TechnologyResponse mapToResponse(TechnologyMaster tech) {
        return new TechnologyResponse(
            tech.getId(),
            tech.getTechnologyName(),
            tech.getDescription(),
            tech.getIsActive(),
            tech.getCreatedAt(),
            tech.getUpdatedAt()
        );
    }
}

