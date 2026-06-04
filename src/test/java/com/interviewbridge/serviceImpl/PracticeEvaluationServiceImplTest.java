package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.EvaluationStatus;
import com.interviewbridge.Enum.QuestionStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.Enum.SessionStatus;
//import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.PracticeQuestion;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.exception.UnauthorizedException;
import com.interviewbridge.repository.PracticeQuestionRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.request.AIEvaluationRequest;
import com.interviewbridge.response.AIEvaluationResponse;
import com.interviewbridge.response.EvaluationResultResponse;
import com.interviewbridge.service.AIEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private User mockUser;
    private PracticeSession mockSession;
    private PracticeQuestion mockQuestion;
    private UUID questionId;
    private UUID sessionId;
    private String email;

    @BeforeEach
    void setUp() {
        email = "user@test.com";
        questionId = UUID.randomUUID();
        sessionId = UUID.randomUUID();

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .name("Test User")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        TechnologyMaster tech = TechnologyMaster.builder()
                .id(UUID.randomUUID())
                .technologyName("Java")
                .isActive(true)
                .build();

        ExperienceMaster exp = ExperienceMaster.builder()
                .id(UUID.randomUUID())
                .experienceLabel("Mid")
                .isActive(true)
                .build();

        mockSession = PracticeSession.builder()
                .id(sessionId)
                .user(mockUser)
                .technology(tech)
                .experience(exp)
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .totalQuestions(2)
                .completedQuestions(1)
                .averageScore(0.0)
                .startedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        mockQuestion = PracticeQuestion.builder()
                .id(questionId)
                .practiceSession(mockSession)
                .questionNumber(1)
                .question("What is polymorphism?")
                .userAnswer("Polymorphism is the ability of an object to take on many forms.")
                .questionStatus(QuestionStatus.ANSWERED)
                .evaluationStatus(EvaluationStatus.PENDING)
                .isActive(true)
                .build();
    }

    @Test
    void evaluateQuestion_Success() {
        // Arrange
        AIEvaluationResponse aiResponse = new AIEvaluationResponse(
            "Translated answer",
            "Improved answer",
            "Detailed explanation",
            8
        );

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        when(aiEvaluationService.evaluateAnswer(any(AIEvaluationRequest.class))).thenReturn(aiResponse);

        List<PracticeQuestion> questionsList = new ArrayList<>();
        questionsList.add(mockQuestion);
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(questionsList);

        // Act
        EvaluationResultResponse response = practiceEvaluationService.evaluateQuestion(email, questionId);

        // Assert
        assertNotNull(response);
        assertEquals(questionId, response.questionId());
        assertEquals("Translated answer", response.translatedAnswer());
        assertEquals("Improved answer", response.improvedAnswer());
        assertEquals("Detailed explanation", response.explanation());
        assertEquals(8, response.score());
        assertEquals(EvaluationStatus.COMPLETED, response.evaluationStatus());
        assertNotNull(response.evaluatedAt());

        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
        verify(practiceSessionRepository, times(1)).save(mockSession);
        assertEquals(8.0, mockSession.getAverageScore());
    }

    @Test
    void evaluateQuestion_ReEvaluationPrevention() {
        // Arrange
        mockQuestion.setEvaluationStatus(EvaluationStatus.COMPLETED);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> 
            practiceEvaluationService.evaluateQuestion(email, questionId)
        );
        verify(aiEvaluationService, never()).evaluateAnswer(any());
    }

    @Test
    void evaluateQuestion_UnauthorizedAccess() {
        // Arrange
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .email("other@test.com")
                .role(Role.ROLE_USER)
                .build();
        mockSession.setUser(otherUser);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            practiceEvaluationService.evaluateQuestion(email, questionId)
        );
        verify(aiEvaluationService, never()).evaluateAnswer(any());
    }

    @Test
    void evaluateQuestion_InvalidQuestionStatus() {
        // Arrange
        mockQuestion.setQuestionStatus(QuestionStatus.PENDING);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> 
            practiceEvaluationService.evaluateQuestion(email, questionId)
        );
        verify(aiEvaluationService, never()).evaluateAnswer(any());
    }

    @Test
    void evaluateQuestion_InvalidScore_TooHigh() {
        // Arrange
        AIEvaluationResponse aiResponse = new AIEvaluationResponse(
            "Translated answer",
            "Improved answer",
            "Detailed explanation",
            12 // Outside [0, 10] range
        );

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        when(aiEvaluationService.evaluateAnswer(any(AIEvaluationRequest.class))).thenReturn(aiResponse);

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> 
            practiceEvaluationService.evaluateQuestion(email, questionId)
        );

        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
        assertEquals(EvaluationStatus.FAILED, mockQuestion.getEvaluationStatus());
    }

    @Test
    void evaluateQuestion_InvalidScore_TooLow() {
        // Arrange
        AIEvaluationResponse aiResponse = new AIEvaluationResponse(
            "Translated answer",
            "Improved answer",
            "Detailed explanation",
            -1 // Outside [0, 10] range
        );

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        when(aiEvaluationService.evaluateAnswer(any(AIEvaluationRequest.class))).thenReturn(aiResponse);

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> 
            practiceEvaluationService.evaluateQuestion(email, questionId)
        );

        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
        assertEquals(EvaluationStatus.FAILED, mockQuestion.getEvaluationStatus());
    }

    @Test
    void evaluateQuestion_AIServiceException() {
        // Arrange
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        when(aiEvaluationService.evaluateAnswer(any(AIEvaluationRequest.class)))
                .thenThrow(new RuntimeException("AI provider unavailable"));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> 
            practiceEvaluationService.evaluateQuestion(email, questionId)
        );

        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
        assertEquals(EvaluationStatus.FAILED, mockQuestion.getEvaluationStatus());
    }

    @Test
    void getEvaluationResults_Success() {
        // Arrange
        mockQuestion.setTranslatedAnswer("Translated");
        mockQuestion.setImprovedAnswer("Improved");
        mockQuestion.setExplanation("Explanation");
        mockQuestion.setScore(7);
        mockQuestion.setEvaluationStatus(EvaluationStatus.COMPLETED);
        mockQuestion.setEvaluatedAt(LocalDateTime.now());

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        // Act
        EvaluationResultResponse response = practiceEvaluationService.getEvaluationResults(email, questionId);

        // Assert
        assertNotNull(response);
        assertEquals(questionId, response.questionId());
        assertEquals("Translated", response.translatedAnswer());
        assertEquals("Improved", response.improvedAnswer());
        assertEquals(7, response.score());
        assertEquals(EvaluationStatus.COMPLETED, response.evaluationStatus());
    }

    @Test
    void getEvaluationResults_NotEvaluated() {
        // Arrange
        mockQuestion.setEvaluationStatus(EvaluationStatus.PENDING);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            practiceEvaluationService.getEvaluationResults(email, questionId)
        );
    }
}
