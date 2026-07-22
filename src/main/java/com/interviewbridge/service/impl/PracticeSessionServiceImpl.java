package com.interviewbridge.service.impl;

import com.interviewbridge.enums.Role;
import com.interviewbridge.enums.SessionStatus;
import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.exception.UnauthorizedException;
import com.interviewbridge.repository.ExperienceMasterRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.TechnologyMasterRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.dto.request.StartPracticeSessionRequest;
import com.interviewbridge.dto.response.PracticeSessionResponse;
import com.interviewbridge.service.PracticeSessionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing practice sessions.
 */
@Service
@RequiredArgsConstructor
public class PracticeSessionServiceImpl implements PracticeSessionService {

    private final UserRepository userRepository;
    private final TechnologyMasterRepository technologyRepository;
    private final ExperienceMasterRepository experienceRepository;
    private final PracticeSessionRepository practiceSessionRepository;

    @Override
    @Transactional
    public PracticeSessionResponse startSession(String email, StartPracticeSessionRequest request) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        TechnologyMaster tech = technologyRepository.findById(request.technologyId())
            .filter(TechnologyMaster::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_TECH_NOT_FOUND + request.technologyId()));

        ExperienceMaster exp = experienceRepository.findById(request.experienceId())
            .filter(ExperienceMaster::getIsActive)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_EXP_NOT_FOUND + request.experienceId()));

        int totalQuestions = request.totalQuestions() != null ? request.totalQuestions() : EntityConstants.PracticeSession.DEFAULT_TOTAL_QUESTIONS;

        PracticeSession session = PracticeSession.builder()
            .user(user)
            .technology(tech)
            .experience(exp)
            .sessionStatus(SessionStatus.CREATED)
            .totalQuestions(totalQuestions)
            .completedQuestions(0)
            .averageScore(0.0)
            .startedAt(LocalDateTime.now())
            .isActive(true)
            .build();

        PracticeSession savedSession = practiceSessionRepository.save(session);
        return mapToResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public PracticeSessionResponse getSessionById(String email, UUID id) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        PracticeSession session = practiceSessionRepository.findByIdWithUserAndTechnologyAndExperience(id)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_SESSION_NOT_FOUND + id));

        User currentUser = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        boolean isOwner = session.getUser().getEmail().equalsIgnoreCase(normalizedEmail);
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(SecurityConstants.MSG_SESSION_ACCESS_DENIED);
        }

        return mapToResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PracticeSessionResponse> getSessionsForUser(String email) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        // Verify user exists
        if (!userRepository.existsByEmail(normalizedEmail)) {
            throw new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail);
        }

        List<PracticeSession> sessions = practiceSessionRepository.findByUserEmailWithUserAndTechnologyAndExperienceOrderByCreatedAtDesc(normalizedEmail);
        return sessions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private PracticeSessionResponse mapToResponse(PracticeSession session) {
        return new PracticeSessionResponse(
            session.getId(),
            session.getUser().getId(),
            session.getUser().getName(),
            session.getTechnology().getId(),
            session.getTechnology().getTechnologyName(),
            session.getExperience().getId(),
            session.getExperience().getExperienceLabel(),
            session.getSessionStatus(),
            session.getTotalQuestions(),
            session.getCompletedQuestions(),
            session.getAverageScore(),
            session.getStartedAt(),
            session.getCompletedAt(),
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }
}

