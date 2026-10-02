package com.interviewbridge.service.impl;

import com.interviewbridge.enums.EvaluationStatus;
import com.interviewbridge.enums.QuestionStatus;
import com.interviewbridge.enums.Role;
import com.interviewbridge.constants.EntityConstants;
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
import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;
import com.interviewbridge.dto.response.EvaluationResultResponse;
import com.interviewbridge.service.AIEvaluationService;
import com.interviewbridge.service.PracticeEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing practice question evaluations.
 */
@Service
@RequiredArgsConstructor
public class PracticeEvaluationServiceImpl implements PracticeEvaluationService {

    private final UserRepository userRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final PracticeQuestionRepository practiceQuestionRepository;
    private final AIEvaluationService aiEvaluationService;

    @Override
    @Transactional
    public EvaluationResultResponse evaluateQuestion(String email, UUID questionId) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        PracticeQuestion question = practiceQuestionRepository.findByIdWithSessionAndUser(questionId)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_QUESTION_NOT_FOUND_ID + questionId));

        PracticeSession session = question.getPracticeSession();

        boolean isOwner = session.getUser().getEmail().equalsIgnoreCase(normalizedEmail);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(SecurityConstants.MSG_SESSION_ACCESS_DENIED);
        }

        if (question.getQuestionStatus() != QuestionStatus.ANSWERED) {
            throw new InvalidOperationException(SecurityConstants.MSG_QUESTION_NOT_ANSWERED);
        }

        if (question.getEvaluationStatus() == EvaluationStatus.COMPLETED) {
            throw new InvalidOperationException(SecurityConstants.MSG_QUESTION_ALREADY_EVALUATED);
        }

        AIEvaluationRequest aiRequest = new AIEvaluationRequest(
            question.getQuestion(),
            question.getUserAnswer(),
            session.getTechnology().getTechnologyName(),
            session.getExperience().getExperienceLabel(),
            question.getReferenceAnswer()
        );

        AIEvaluationResponse aiResponse;
        try {
            aiResponse = aiEvaluationService.evaluateAnswer(aiRequest);
        } catch (Exception e) {
            question.setEvaluationStatus(EvaluationStatus.FAILED);
            practiceQuestionRepository.save(question);
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EVALUATION_FAILED + e.getMessage());
        }

        // Validate score is within 0 to 10 range
        if (aiResponse.score() == null || aiResponse.score() < EntityConstants.PracticeQuestion.MIN_SCORE ||
                aiResponse.score() > EntityConstants.PracticeQuestion.MAX_SCORE) {
            question.setEvaluationStatus(EvaluationStatus.FAILED);
            practiceQuestionRepository.save(question);
            throw new InvalidOperationException(SecurityConstants.MSG_INVALID_SCORE_RANGE);
        }

        // Persist evaluation results
        question.setTranslatedAnswer(aiResponse.translatedAnswer());
        question.setImprovedAnswer(aiResponse.improvedAnswer());
        question.setExplanation(aiResponse.explanation());
        question.setScore(aiResponse.score());
        question.setWhatWasCorrect(aiResponse.whatWasCorrect());
        question.setWhatWasMissing(aiResponse.whatWasMissing());
        question.setEvaluationStatus(EvaluationStatus.COMPLETED);
        question.setEvaluatedAt(LocalDateTime.now());
        practiceQuestionRepository.save(question);

        // Recalculate session averageScore
        List<PracticeQuestion> sessionQuestions = practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(session.getId());
        double sum = 0.0;
        int evaluatedCount = 0;
        for (PracticeQuestion q : sessionQuestions) {
            // Count current updated question too, since findByPracticeSessionId will load it from context/DB
            Integer currentScore = q.getId().equals(questionId) ? question.getScore() : q.getScore();
            if (currentScore != null) {
                sum += currentScore;
                evaluatedCount++;
            }
        }

        if (evaluatedCount > 0) {
            session.setAverageScore(sum / evaluatedCount);
        } else {
            session.setAverageScore(0.0);
        }
        practiceSessionRepository.save(session);

        return mapToResponse(question);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResultResponse getEvaluationResults(String email, UUID questionId) {
        String normalizedEmail = email != null ? email.trim().toLowerCase() : null;
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND + normalizedEmail));

        PracticeQuestion question = practiceQuestionRepository.findByIdWithSessionAndUser(questionId)
            .orElseThrow(() -> new ResourceNotFoundException(SecurityConstants.MSG_QUESTION_NOT_FOUND_ID + questionId));

        PracticeSession session = question.getPracticeSession();

        boolean isOwner = session.getUser().getEmail().equalsIgnoreCase(normalizedEmail);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException(SecurityConstants.MSG_SESSION_ACCESS_DENIED);
        }

        if (question.getEvaluationStatus() != EvaluationStatus.COMPLETED || question.getScore() == null) {
            throw new ResourceNotFoundException(SecurityConstants.MSG_EVALUATION_NOT_FOUND);
        }

        return mapToResponse(question);
    }

    private EvaluationResultResponse mapToResponse(PracticeQuestion question) {
        return new EvaluationResultResponse(
            question.getId(),
            question.getPracticeSession().getId(),
            question.getQuestionNumber(),
            question.getQuestion(),
            question.getUserAnswer(),
            question.getTranslatedAnswer(),
            question.getImprovedAnswer(),
            question.getExplanation(),
            question.getScore(),
            question.getWhatWasCorrect(),
            question.getWhatWasMissing(),
            question.getEvaluationStatus(),
            question.getEvaluatedAt()
        );
    }
}

