package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.QuestionStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.Enum.SessionStatus;
//import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.PracticeQuestion;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.exception.UnauthorizedException;
import com.interviewbridge.repository.PracticeQuestionRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.request.SubmitAnswerRequest;
import com.interviewbridge.response.PracticeQuestionResponse;
import com.interviewbridge.response.SubmitAnswerResponse;
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
class PracticeAnswerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @Mock
    private PracticeQuestionRepository practiceQuestionRepository;

    @InjectMocks
    private PracticeAnswerServiceImpl practiceAnswerService;

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

        mockSession = PracticeSession.builder()
                .id(sessionId)
                .user(mockUser)
                .sessionStatus(SessionStatus.IN_PROGRESS)
                .totalQuestions(2)
                .completedQuestions(0)
                .startedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        mockQuestion = PracticeQuestion.builder()
                .id(questionId)
                .practiceSession(mockSession)
                .questionNumber(1)
                .question("What is polymorphism?")
                .questionStatus(QuestionStatus.PENDING)
                .isActive(true)
                .build();
    }

    @Test
    void submitAnswer_Success_NotCompleted() {
        // Arrange
        SubmitAnswerRequest request = new SubmitAnswerRequest("Polymorphism is...");
        
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        
        List<PracticeQuestion> questionsList = new ArrayList<>();
        questionsList.add(mockQuestion);
        questionsList.add(PracticeQuestion.builder()
                .id(UUID.randomUUID())
                .practiceSession(mockSession)
                .questionNumber(2)
                .question("What is inheritance?")
                .questionStatus(QuestionStatus.PENDING)
                .isActive(true)
                .build());
        
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(questionsList);

        // Act
        SubmitAnswerResponse response = practiceAnswerService.submitAnswer(email, questionId, request);

        // Assert
        assertNotNull(response);
        assertEquals(questionId, response.questionId());
        assertEquals(sessionId, response.sessionId());
        assertEquals(1, response.questionNumber());
        assertEquals("Polymorphism is...", response.userAnswer());
        assertEquals(QuestionStatus.ANSWERED, response.questionStatus());
        assertEquals(1, response.completedQuestions());
        assertEquals(SessionStatus.IN_PROGRESS, response.sessionStatus());

        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
        verify(practiceSessionRepository, times(1)).save(mockSession);
        assertEquals(1, mockSession.getCompletedQuestions());
        assertEquals(SessionStatus.IN_PROGRESS, mockSession.getSessionStatus());
        assertNull(mockSession.getCompletedAt());
    }

    @Test
    void submitAnswer_Success_CompletesSession() {
        // Arrange
        SubmitAnswerRequest request = new SubmitAnswerRequest("Polymorphism is...");
        
        // Update mock session to have 1 completed question, total 2
        mockSession.setCompletedQuestions(1);
        
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        
        List<PracticeQuestion> questionsList = new ArrayList<>();
        // Current question being answered
        questionsList.add(mockQuestion);
        // Already answered question
        questionsList.add(PracticeQuestion.builder()
                .id(UUID.randomUUID())
                .practiceSession(mockSession)
                .questionNumber(2)
                .question("What is inheritance?")
                .questionStatus(QuestionStatus.ANSWERED)
                .isActive(true)
                .build());
        
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(questionsList);

        // Act
        SubmitAnswerResponse response = practiceAnswerService.submitAnswer(email, questionId, request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.completedQuestions());
        assertEquals(SessionStatus.COMPLETED, response.sessionStatus());

        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
        verify(practiceSessionRepository, times(1)).save(mockSession);
        assertEquals(2, mockSession.getCompletedQuestions());
        assertEquals(SessionStatus.COMPLETED, mockSession.getSessionStatus());
        assertNotNull(mockSession.getCompletedAt());
    }

    @Test
    void submitAnswer_UserNotFound() {
        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer text");
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            practiceAnswerService.submitAnswer(email, questionId, request)
        );
        verify(practiceQuestionRepository, never()).save(any());
    }

    @Test
    void submitAnswer_QuestionNotFound() {
        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer text");
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            practiceAnswerService.submitAnswer(email, questionId, request)
        );
        verify(practiceQuestionRepository, never()).save(any());
    }

    @Test
    void submitAnswer_UnauthorizedAccess() {
        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer text");
        
        // Another user who is not owner and not admin
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .email("other@test.com")
                .role(Role.ROLE_USER)
                .build();
        
        mockSession.setUser(otherUser);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        assertThrows(UnauthorizedException.class, () -> 
            practiceAnswerService.submitAnswer(email, questionId, request)
        );
        verify(practiceQuestionRepository, never()).save(any());
    }

    @Test
    void submitAnswer_AdminAccessAllowed() {
        SubmitAnswerRequest request = new SubmitAnswerRequest("Admin submits...");
        
        // Admin user
        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .isActive(true)
                .build();

        // Session belongs to regular user, but admin is performing request
        mockSession.setUser(mockUser);

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        
        List<PracticeQuestion> questionsList = new ArrayList<>();
        questionsList.add(mockQuestion);
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(questionsList);

        SubmitAnswerResponse response = practiceAnswerService.submitAnswer("admin@test.com", questionId, request);

        assertNotNull(response);
        verify(practiceQuestionRepository, times(1)).save(mockQuestion);
    }

    @Test
    void submitAnswer_SessionNotInProgress() {
        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer text");
        mockSession.setSessionStatus(SessionStatus.CREATED);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        assertThrows(InvalidOperationException.class, () -> 
            practiceAnswerService.submitAnswer(email, questionId, request)
        );
        verify(practiceQuestionRepository, never()).save(any());
    }

    @Test
    void submitAnswer_QuestionAlreadyAnswered() {
        SubmitAnswerRequest request = new SubmitAnswerRequest("Answer text");
        mockQuestion.setQuestionStatus(QuestionStatus.ANSWERED);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        assertThrows(InvalidOperationException.class, () -> 
            practiceAnswerService.submitAnswer(email, questionId, request)
        );
        verify(practiceQuestionRepository, never()).save(any());
    }

    @Test
    void getQuestionDetails_Success() {
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        PracticeQuestionResponse response = practiceAnswerService.getQuestionDetails(email, questionId);

        assertNotNull(response);
        assertEquals(questionId, response.id());
        assertEquals(sessionId, response.practiceSessionId());
        assertEquals(QuestionStatus.PENDING, response.questionStatus());
    }

    @Test
    void getQuestionDetails_Unauthorized() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .email("other@test.com")
                .role(Role.ROLE_USER)
                .build();
        
        mockSession.setUser(otherUser);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));

        assertThrows(UnauthorizedException.class, () -> 
            practiceAnswerService.getQuestionDetails(email, questionId)
        );
    }

    @Test
    void submitAnswer_OptimisticLockingConflict() {
        // Arrange
        SubmitAnswerRequest request = new SubmitAnswerRequest("Polymorphism is...");
        
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceQuestionRepository.findByIdWithSessionAndUser(questionId)).thenReturn(Optional.of(mockQuestion));
        
        List<PracticeQuestion> questionsList = new ArrayList<>();
        questionsList.add(mockQuestion);
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(questionsList);
        
        // Mock save throwing OptimisticLockingFailureException
        when(practiceSessionRepository.save(any(PracticeSession.class)))
                .thenThrow(new org.springframework.dao.OptimisticLockingFailureException("Optimistic lock conflict"));

        // Act & Assert
        assertThrows(org.springframework.dao.OptimisticLockingFailureException.class, () -> 
            practiceAnswerService.submitAnswer(email, questionId, request)
        );
    }
}
