package com.interviewbridge.service.impl;

import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.ExperienceMasterRepository;
import com.interviewbridge.repository.TechnologyMasterRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.dto.request.UserProfileSetupRequest;
import com.interviewbridge.dto.response.ExperienceResponse;
import com.interviewbridge.dto.response.TechnologyResponse;
import com.interviewbridge.dto.response.UserProfileResponse;
import com.interviewbridge.dto.response.UserStatusResponse;
import com.interviewbridge.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing user profile details.
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final TechnologyMasterRepository technologyRepository;
    private final ExperienceMasterRepository experienceRepository;

    @Override
    @Transactional
    public UserProfileResponse setupProfile(String email, UserProfileSetupRequest request) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        if (user.getTechnology() != null || user.getExperience() != null) {
            throw new DuplicateResourceException(SecurityConstants.MSG_PROFILE_ALREADY_SETUP);
        }

        if (request.technologyId() == null) {
            throw new InvalidOperationException(EntityConstants.User.MSG_TECH_ID_REQUIRED);
        }
        if (request.experienceId() == null) {
            throw new InvalidOperationException(EntityConstants.User.MSG_EXP_ID_REQUIRED);
        }

        TechnologyMaster tech = technologyRepository.findById(request.technologyId())
            .filter(TechnologyMaster::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + request.technologyId()));

        ExperienceMaster exp = experienceRepository.findById(request.experienceId())
            .filter(ExperienceMaster::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + request.experienceId()));

        user.setTechnology(tech);
        user.setExperience(exp);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UserProfileSetupRequest request) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        if (request.name() != null) {
            String trimmedName = request.name().trim();
            if (trimmedName.isEmpty()) {
                throw new InvalidOperationException(EntityConstants.User.MSG_NAME_BLANK);
            }
            if (trimmedName.length() > EntityConstants.User.NAME_MAX_LENGTH) {
                throw new InvalidOperationException(EntityConstants.User.MSG_NAME_SIZE);
            }
            user.setName(trimmedName);
        }

        if (request.technologyId() != null) {
            TechnologyMaster tech = technologyRepository.findById(request.technologyId())
                .filter(TechnologyMaster::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + request.technologyId()));
            user.setTechnology(tech);
        }

        if (request.experienceId() != null) {
            ExperienceMaster exp = experienceRepository.findById(request.experienceId())
                .filter(ExperienceMaster::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + request.experienceId()));
            user.setExperience(exp);
        }

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusResponse getUserStatus(String email) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));
        return new UserStatusResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole(),
            user.getApprovalStatus(),
            user.getIsActive()
        );
    }

    private UserProfileResponse mapToResponse(User user) {
        TechnologyResponse techResponse = user.getTechnology() != null ?
            new TechnologyResponse(
                user.getTechnology().getId(),
                user.getTechnology().getTechnologyName(),
                user.getTechnology().getDescription(),
                user.getTechnology().getIsActive(),
                user.getTechnology().getCreatedAt(),
                user.getTechnology().getUpdatedAt()
            ) : null;

        ExperienceResponse expResponse = user.getExperience() != null ?
            new ExperienceResponse(
                user.getExperience().getId(),
                user.getExperience().getExperienceLabel(),
                user.getExperience().getIsActive(),
                user.getExperience().getCreatedAt(),
                user.getExperience().getUpdatedAt()
            ) : null;

        return new UserProfileResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole(),
            techResponse,
            expResponse
        );
    }
}

