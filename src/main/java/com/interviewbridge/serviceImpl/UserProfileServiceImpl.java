package com.interviewbridge.serviceImpl;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.ExperienceMasterRepository;
import com.interviewbridge.repository.TechnologyMasterRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.request.UserProfileSetupRequest;
import com.interviewbridge.response.ExperienceResponse;
import com.interviewbridge.response.TechnologyResponse;
import com.interviewbridge.response.UserProfileResponse;
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

        if (user.getTechnology() == null && user.getExperience() == null) {
            throw new ResourceNotFoundException(SecurityConstants.MSG_PROFILE_NOT_SETUP);
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
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));
        return mapToResponse(user);
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
            user.getRole(),
            techResponse,
            expResponse
        );
    }
}
