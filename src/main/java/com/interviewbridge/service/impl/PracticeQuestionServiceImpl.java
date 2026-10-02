package com.interviewbridge.service.impl;

import com.interviewbridge.enums.QuestionStatus;
import com.interviewbridge.enums.Role;
import com.interviewbridge.enums.SessionStatus;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.PracticeQuestion;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.exception.UnauthorizedException;
import com.interviewbridge.repository.PracticeQuestionRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.dto.request.AIQuestionRequest;
import com.interviewbridge.dto.response.AIQuestionResponse;
import com.interviewbridge.dto.response.PracticeQuestionResponse;
import com.interviewbridge.service.AIQuestionGenerationService;
import com.interviewbridge.service.PracticeQuestionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing practice questions and interacting with the AI question generator.
 */
@Service
@RequiredArgsConstructor
public class PracticeQuestionServiceImpl implements PracticeQuestionService {

    private final UserRepository userRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final PracticeQuestionRepository practiceQuestionRepository;
    private final AIQuestionGenerationService aiQuestionGenerationService;

    @Override
    @Transactional
    public List<PracticeQuestionResponse> generateAndSaveQuestions(String email, UUID sessionId) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        PracticeSession session = practiceSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_SESSION_NOT_FOUND + sessionId));

        boolean isOwner = session.getUser().getEmail().equalsIgnoreCase(normalizedEmail);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(SecurityConstants.MSG_SESSION_ACCESS_DENIED);
        }

        if (session.getSessionStatus() != SessionStatus.CREATED) {
            throw new InvalidOperationException(SecurityConstants.MSG_QUESTIONS_GENERATED_CREATED_ONLY);
        }

        List<PracticeQuestion> existingQuestions = practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId);
        if (!existingQuestions.isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_QUESTIONS_ALREADY_GENERATED);
        }

        AIQuestionRequest aiRequest = new AIQuestionRequest(
            session.getTechnology().getTechnologyName(),
            session.getExperience().getExperienceLabel(),
            session.getTotalQuestions()
        );

        List<AIQuestionResponse> aiResponses = aiQuestionGenerationService.generateQuestions(aiRequest);

        List<PracticeQuestion> questionsToSave = aiResponses.stream()
            .map(aiResp -> PracticeQuestion.builder()
                .practiceSession(session)
                .questionNumber(aiResp.questionNumber())
                .question(aiResp.questionText())
                .referenceAnswer(aiResp.referenceAnswer())
                .questionStatus(QuestionStatus.PENDING)
                .isActive(true)
                .build())
            .collect(Collectors.toList());

        List<PracticeQuestion> savedQuestions = practiceQuestionRepository.saveAll(questionsToSave);

        session.setSessionStatus(SessionStatus.IN_PROGRESS);
        practiceSessionRepository.save(session);

        return savedQuestions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PracticeQuestionResponse> getQuestionsForSession(String email, UUID sessionId) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        PracticeSession session = practiceSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_SESSION_NOT_FOUND + sessionId));

        boolean isOwner = session.getUser().getEmail().equalsIgnoreCase(normalizedEmail);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(SecurityConstants.MSG_SESSION_ACCESS_DENIED);
        }

        List<PracticeQuestion> questions = practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId);
        return questions.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PracticeQuestionResponse getQuestionByNumber(String email, UUID sessionId, Integer questionNumber) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        PracticeSession session = practiceSessionRepository.findById(sessionId)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_SESSION_NOT_FOUND + sessionId));

        boolean isOwner = session.getUser().getEmail().equalsIgnoreCase(normalizedEmail);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(SecurityConstants.MSG_SESSION_ACCESS_DENIED);
        }

        List<PracticeQuestion> questions = practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId);
        PracticeQuestion question = questions.stream()
            .filter(q -> q.getQuestionNumber().equals(questionNumber))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_QUESTION_NOT_FOUND + questionNumber));

        return mapToResponse(question);
    }

    private PracticeQuestionResponse mapToResponse(PracticeQuestion question) {
        return new PracticeQuestionResponse(
            question.getId(),
            question.getPracticeSession().getId(),
            question.getQuestionNumber(),
            question.getQuestion(),
            question.getReferenceAnswer(),
            question.getUserAnswer(),
            question.getTranslatedAnswer(),
            question.getImprovedAnswer(),
            question.getExplanation(),
            question.getScore(),
            question.getQuestionStatus(),
            question.getEvaluationStatus(),
            question.getEvaluatedAt(),
            question.getCreatedAt(),
            question.getUpdatedAt()
        );
    }
}

