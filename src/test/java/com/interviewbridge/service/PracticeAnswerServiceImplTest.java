package com.interviewbridge.service;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.SubmitAnswerRequest;
import com.interviewbridge.dto.response.SubmitAnswerResponse;
import com.interviewbridge.entity.PracticeQuestion;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.User;
import com.interviewbridge.enums.EvaluationStatus;
import com.interviewbridge.enums.QuestionStatus;
import com.interviewbridge.enums.Role;
import com.interviewbridge.enums.SessionStatus;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.UnauthorizedException;
import com.interviewbridge.repository.PracticeQuestionRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.service.impl.PracticeAnswerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PracticeAnswerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @Mock
    private PracticeQuestionRepository practiceQuestionRepository;

    @InjectMocks
    private PracticeAnswerServiceImpl practiceAnswerService;

    private User testUser;
    private PracticeSession testSession;
    private PracticeQuestion testQuestion;
    private UUID questionId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        questionId = UUID.randomUUID();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .role(Role.ROLE_USER)
                .build();

        testSession = PracticeSession.builder()
                .id(sessionId)
                .user(testUser)
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .totalQuestions(3)
                .completedQuestions(0)
                .build();

        testQuestion = PracticeQuestion.builder()
                .id(questionId)
                .practiceSession(testSession)
                .questionNumber(1)
                .question("What is HashMap?")
                .referenceAnswer("HashMap uses hashing...")
                .questionStatus(QuestionStatus.PENDING)
                .evaluationStatus(EvaluationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("First submission increments completedQuestions and sets status to ANSWERED")
    void testFirstSubmission_IncrementsCompletedQuestions() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(List.of(testQuestion));

        SubmitAnswerRequest request = new SubmitAnswerRequest("Initial answer");
        SubmitAnswerResponse response = practiceAnswerService.submitAnswer("user@example.com", questionId, request);

        assertNotNull(response);
        assertEquals(QuestionStatus.ANSWERED, response.questionStatus());
        assertEquals("Initial answer", response.userAnswer());
        assertEquals(1, testSession.getCompletedQuestions());
        verify(practiceQuestionRepository).save(testQuestion);
        verify(practiceSessionRepository).save(testSession);
    }

    @Test
    @DisplayName("Resubmission updates userAnswer without incrementing completedQuestions")
    void testResubmission_UpdatesAnswer_DoesNotIncrementCompletedCount() {
        testQuestion.setQuestionStatus(QuestionStatus.ANSWERED);
        testQuestion.setUserAnswer("Old answer");
        testSession.setCompletedQuestions(1);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        SubmitAnswerRequest request = new SubmitAnswerRequest("Updated improved answer");
        SubmitAnswerResponse response = practiceAnswerService.submitAnswer("user@example.com", questionId, request);

        assertNotNull(response);
        assertEquals("Updated improved answer", testQuestion.getUserAnswer());
        assertEquals(QuestionStatus.ANSWERED, testQuestion.getQuestionStatus());
        assertEquals(1, testSession.getCompletedQuestions()); // unchanged
        verify(practiceQuestionRepository).save(testQuestion);
        verify(practiceSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Resubmission resets stale evaluation state when evaluation had previously failed or had stale fields")
    void testResubmission_ResetsStaleEvaluationFields() {
        testQuestion.setQuestionStatus(QuestionStatus.ANSWERED);
        testQuestion.setUserAnswer("Old answer");
        testQuestion.setEvaluationStatus(EvaluationStatus.FAILED);
        testQuestion.setScore(3);
        testQuestion.setExplanation("Old feedback");
        testQuestion.setEvaluatedAt(LocalDateTime.now());
        testSession.setCompletedQuestions(1);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        SubmitAnswerRequest request = new SubmitAnswerRequest("Brand new answer");
        SubmitAnswerResponse response = practiceAnswerService.submitAnswer("user@example.com", questionId, request);

        assertNotNull(response);
        assertEquals("Brand new answer", testQuestion.getUserAnswer());
        assertEquals(EvaluationStatus.PENDING, testQuestion.getEvaluationStatus());
        assertNull(testQuestion.getScore());
        assertNull(testQuestion.getExplanation());
        assertNull(testQuestion.getEvaluatedAt());
        verify(practiceQuestionRepository).save(testQuestion);
    }

    @Test
    @DisplayName("Completed evaluation rejects resubmission")
    void testResubmission_WhenEvaluationCompleted_ThrowsException() {
        testQuestion.setQuestionStatus(QuestionStatus.ANSWERED);
        testQuestion.setUserAnswer("Evaluated answer");
        testQuestion.setEvaluationStatus(EvaluationStatus.COMPLETED);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        SubmitAnswerRequest request = new SubmitAnswerRequest("Attempt to edit evaluated answer");

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () ->
                practiceAnswerService.submitAnswer("user@example.com", questionId, request));

        assertEquals(SecurityConstants.MSG_QUESTION_ALREADY_ANSWERED, ex.getMessage());
        verify(practiceQuestionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unauthorized user cannot submit answer")
    void testSubmitAnswer_UnauthorizedUser_ThrowsException() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .email("other@example.com")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer");

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                practiceAnswerService.submitAnswer("other@example.com", questionId, request));

        assertEquals(SecurityConstants.MSG_SESSION_ACCESS_DENIED, ex.getMessage());
    }

    @Test
    @DisplayName("Session not in progress or completed throws MSG_SESSION_NOT_IN_PROGRESS")
    void testSubmitAnswer_SessionCreated_ThrowsException() {
        testSession.setSessionStatus(SessionStatus.CREATED);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer");

        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () ->
                practiceAnswerService.submitAnswer("user@example.com", questionId, request));

        assertEquals(SecurityConstants.MSG_SESSION_NOT_IN_PROGRESS, ex.getMessage());
    }
}
