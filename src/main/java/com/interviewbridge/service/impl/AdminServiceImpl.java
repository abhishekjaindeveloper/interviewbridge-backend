package com.interviewbridge.service.impl;

import com.interviewbridge.enums.ApprovalStatus;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.dto.response.AdminUserResponse;
import com.interviewbridge.dto.response.ExperienceResponse;
import com.interviewbridge.dto.response.TechnologyResponse;
import com.interviewbridge.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewbridge.dto.response.AdminUserStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of AdminService defining admin user approval business rules.
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getPendingUsers() {
        return userRepository.findByApprovalStatus(ApprovalStatus.PENDING)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND_ID + id));
        
        if (user.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new InvalidOperationException(SecurityConstants.MSG_INVALID_APPROVAL_STATUS);
        }
        
        user.setApprovalStatus(ApprovalStatus.APPROVED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void rejectUser(UUID id, String reason) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND_ID + id));
        
        if (user.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new InvalidOperationException(SecurityConstants.MSG_INVALID_APPROVAL_STATUS);
        }
        
        user.setApprovalStatus(ApprovalStatus.REJECTED);
        user.setRejectionReason(reason);
        user.setRejectedAt(java.time.LocalDateTime.now());
        
        String adminEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        user.setRejectedBy(adminEmail);
        
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsers(Pageable pageable, ApprovalStatus approvalStatus, Boolean isActive, String search) {
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findAllFilteredAndSearched(search, approvalStatus, isActive, loggedInEmail, pageable)
            .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND_ID + id));
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getEmail().equalsIgnoreCase(loggedInEmail)) {
            throw new InvalidOperationException("You cannot activate or deactivate your own administrator account.");
        }
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND_ID + id));
        String loggedInEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getEmail().equalsIgnoreCase(loggedInEmail)) {
            throw new InvalidOperationException("You cannot activate or deactivate your own administrator account.");
        }
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserStatisticsResponse getUserStatistics() {
        long total = userRepository.countByApprovalStatusNot(ApprovalStatus.REJECTED);
        long active = userRepository.countByIsActiveAndApprovalStatus(true, ApprovalStatus.APPROVED);
        long inactive = userRepository.countByIsActiveAndApprovalStatus(false, ApprovalStatus.APPROVED);
        long pending = userRepository.countByApprovalStatus(ApprovalStatus.PENDING);
        return new AdminUserStatisticsResponse(total, active, inactive, pending);
    }

    private AdminUserResponse mapToResponse(User user) {
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

        return new AdminUserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole(),
            user.getApprovalStatus(),
            techResponse,
            expResponse,
            user.getIsActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}

