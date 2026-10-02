package com.interviewbridge.service;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;
import com.interviewbridge.dto.response.EvaluationResultResponse;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.PracticeQuestion;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.enums.EvaluationStatus;
import com.interviewbridge.enums.QuestionStatus;
import com.interviewbridge.enums.Role;
import com.interviewbridge.enums.SessionStatus;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.repository.PracticeQuestionRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.service.impl.PracticeEvaluationServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PracticeEvaluationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @Mock
    private PracticeQuestionRepository practiceQuestionRepository;

    @Mock
    private AIEvaluationService aiEvaluationService;

    @InjectMocks
    private PracticeEvaluationServiceImpl practiceEvaluationService;

    private User testUser;
    private PracticeSession testSession;
    private PracticeQuestion testQuestion;
    private UUID questionId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        questionId = UUID.randomUUID();
        sessionId = UUID.randomUUID();

        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .role(Role.ROLE_USER)
            .build();

        TechnologyMaster tech = TechnologyMaster.builder()
            .technologyName("Java")
            .build();

        ExperienceMaster exp = ExperienceMaster.builder()
            .experienceLabel("Senior")
            .build();

        testSession = PracticeSession.builder()
            .id(sessionId)
            .user(testUser)
            .technology(tech)
            .experience(exp)
            .totalQuestions(2)
            .completedQuestions(2)
            .sessionStatus(SessionStatus.IN_PROGRESS)
            .averageScore(0.0)
            .build();

        testQuestion = PracticeQuestion.builder()
            .id(questionId)
            .practiceSession(testSession)
            .questionNumber(1)
            .question("Explain memory leaks in Java.")
            .referenceAnswer("Memory leaks occur when objects are no longer needed but remain reachable via strong references from active GC roots.")
            .userAnswer("Objects remain referenced in static maps and cannot be garbage collected.")
            .questionStatus(QuestionStatus.ANSWERED)
            .evaluationStatus(EvaluationStatus.PENDING)
            .build();
    }

    @Test
    @DisplayName("K, L, M, N, O, P. Successful evaluation persists feedback, score, status, timestamp and recalculates session average")
    void evaluateQuestion_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        AIEvaluationResponse mockAiResponse = new AIEvaluationResponse(
            "Translated answer: Objects remain referenced in static maps...",
            "Improved answer: Memory leaks occur when unneeded objects maintain strong references...",
            "Good understanding of GC root reachability.",
            9,
            "Correctly identified static collections as common leak vectors.",
            "Could mention profiling tools like VisualVM or Eclipse Memory Analyzer."
        );
        when(aiEvaluationService.evaluateAnswer(any(AIEvaluationRequest.class))).thenReturn(mockAiResponse);

        PracticeQuestion secondQuestion = PracticeQuestion.builder()
            .id(UUID.randomUUID())
            .practiceSession(testSession)
            .questionNumber(2)
            .score(7)
            .questionStatus(QuestionStatus.ANSWERED)
            .evaluationStatus(EvaluationStatus.COMPLETED)
            .build();

        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
            .thenReturn(List.of(testQuestion, secondQuestion));

        EvaluationResultResponse response = practiceEvaluationService.evaluateQuestion("test@example.com", questionId);

        assertNotNull(response);
        assertEquals(questionId, response.questionId());
        assertEquals(9, response.score());
        assertEquals(EvaluationStatus.COMPLETED, response.evaluationStatus());
        assertNotNull(response.evaluatedAt());
        assertEquals("Correctly identified static collections as common leak vectors.", response.whatWasCorrect());
        assertEquals("Could mention profiling tools like VisualVM or Eclipse Memory Analyzer.", response.whatWasMissing());

        // Verify entity persistence
        assertEquals(EvaluationStatus.COMPLETED, testQuestion.getEvaluationStatus());
        assertEquals(9, testQuestion.getScore());
        assertEquals("Correctly identified static collections as common leak vectors.", testQuestion.getWhatWasCorrect());
        assertEquals("Could mention profiling tools like VisualVM or Eclipse Memory Analyzer.", testQuestion.getWhatWasMissing());
        assertNotNull(testQuestion.getEvaluatedAt());
        verify(practiceQuestionRepository).save(testQuestion);

        // Verify session average recalculation: (9 + 7) / 2 = 8.0
        assertEquals(8.0, testSession.getAverageScore());
        verify(practiceSessionRepository).save(testSession);
    }

    @Test
    @DisplayName("Q. getEvaluationResults returns stored evaluation details including whatWasCorrect and whatWasMissing")
    void getEvaluationResults_success() {
        testQuestion.setEvaluationStatus(EvaluationStatus.COMPLETED);
        testQuestion.setScore(9);
        testQuestion.setTranslatedAnswer("Translated");
        testQuestion.setImprovedAnswer("Improved");
        testQuestion.setExplanation("Explanation");
        testQuestion.setWhatWasCorrect("Correct points");
        testQuestion.setWhatWasMissing("Missing points");
        testQuestion.setEvaluatedAt(LocalDateTime.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        EvaluationResultResponse response = practiceEvaluationService.getEvaluationResults("test@example.com", questionId);

        assertNotNull(response);
        assertEquals(9, response.score());
        assertEquals("Correct points", response.whatWasCorrect());
        assertEquals("Missing points", response.whatWasMissing());
        assertEquals(EvaluationStatus.COMPLETED, response.evaluationStatus());
    }

    @Test
    @DisplayName("Evaluation fails when question has not been answered")
    void evaluateQuestion_notAnswered_throwsException() {
        testQuestion.setQuestionStatus(QuestionStatus.PENDING);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        InvalidOperationException ex = assertThrows(
            InvalidOperationException.class,
            () -> practiceEvaluationService.evaluateQuestion("test@example.com", questionId)
        );

        assertEquals(SecurityConstants.MSG_QUESTION_NOT_ANSWERED, ex.getMessage());
    }

    @Test
    @DisplayName("Evaluation fails when question has already been evaluated")
    void evaluateQuestion_alreadyEvaluated_throwsException() {
        testQuestion.setEvaluationStatus(EvaluationStatus.COMPLETED);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(testQuestion));

        InvalidOperationException ex = assertThrows(
            InvalidOperationException.class,
            () -> practiceEvaluationService.evaluateQuestion("test@example.com", questionId)
        );

        assertEquals(SecurityConstants.MSG_QUESTION_ALREADY_EVALUATED, ex.getMessage());
    }
}
