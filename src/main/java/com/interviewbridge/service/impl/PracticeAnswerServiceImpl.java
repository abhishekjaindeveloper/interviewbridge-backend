package com.interviewbridge.service.impl;

import com.interviewbridge.enums.EvaluationStatus;
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
import com.interviewbridge.dto.request.SubmitAnswerRequest;
import com.interviewbridge.dto.response.PracticeQuestionResponse;
import com.interviewbridge.dto.response.SubmitAnswerResponse;
import com.interviewbridge.service.PracticeAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing practice question answers.
 */
@Service
@RequiredArgsConstructor
public class PracticeAnswerServiceImpl implements PracticeAnswerService {

    private final UserRepository userRepository;
    private final PracticeSessionRepository practiceSessionRepository;
    private final PracticeQuestionRepository practiceQuestionRepository;

    @Override
    @Transactional
    public SubmitAnswerResponse submitAnswer(String email, UUID questionId, SubmitAnswerRequest request) {
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

        if (session.getSessionStatus() != SessionStatus.IN_PROGRESS && session.getSessionStatus() != SessionStatus.COMPLETED) {
            throw new InvalidOperationException(SecurityConstants.MSG_SESSION_NOT_IN_PROGRESS);
        }

        if (question.getEvaluationStatus() == EvaluationStatus.COMPLETED) {
            throw new InvalidOperationException(SecurityConstants.MSG_QUESTION_ALREADY_ANSWERED);
        }

        boolean isResubmission = question.getQuestionStatus() == QuestionStatus.ANSWERED;

        // Save userAnswer and update status
        question.setUserAnswer(request.answer());
        question.setQuestionStatus(QuestionStatus.ANSWERED);

        if (isResubmission) {
            // Invalidate/reset stale evaluation data if evaluationStatus is FAILED or partially present
            if (question.getEvaluationStatus() == EvaluationStatus.FAILED
                    || question.getScore() != null
                    || question.getEvaluatedAt() != null
                    || question.getExplanation() != null
                    || question.getTranslatedAnswer() != null
                    || question.getImprovedAnswer() != null
                    || question.getWhatWasCorrect() != null
                    || question.getWhatWasMissing() != null) {
                question.setEvaluationStatus(EvaluationStatus.PENDING);
                question.setEvaluatedAt(null);
                question.setScore(null);
                question.setTranslatedAnswer(null);
                question.setImprovedAnswer(null);
                question.setExplanation(null);
                question.setWhatWasCorrect(null);
                question.setWhatWasMissing(null);
            }
        }

        practiceQuestionRepository.save(question);

        if (!isResubmission) {
            // Retrieve all questions for the session to update completion status accurately
            List<PracticeQuestion> questions = practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(session.getId());

            int completedCount = 0;
            for (PracticeQuestion q : questions) {
                if (q.getId().equals(questionId)) {
                    completedCount++;
                } else if (q.getQuestionStatus() == QuestionStatus.ANSWERED) {
                    completedCount++;
                }
            }

            session.setCompletedQuestions(completedCount);

            if (completedCount >= session.getTotalQuestions()) {
                session.setSessionStatus(SessionStatus.COMPLETED);
                session.setCompletedAt(LocalDateTime.now());
            }

            practiceSessionRepository.save(session);
        }

        return new SubmitAnswerResponse(
            question.getId(),
            session.getId(),
            question.getQuestionNumber(),
            question.getQuestion(),
            question.getUserAnswer(),
            question.getQuestionStatus(),
            session.getCompletedQuestions(),
            session.getTotalQuestions(),
            session.getSessionStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PracticeQuestionResponse getQuestionDetails(String email, UUID questionId) {
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

