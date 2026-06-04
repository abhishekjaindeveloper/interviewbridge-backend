package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.QuestionStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.Enum.SessionStatus;
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
import com.interviewbridge.request.AIQuestionRequest;
import com.interviewbridge.response.AIQuestionResponse;
import com.interviewbridge.response.PracticeQuestionResponse;
import com.interviewbridge.service.AIQuestionGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
//import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PracticeQuestionServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @Mock
    private PracticeQuestionRepository practiceQuestionRepository;

    @Mock
    private AIQuestionGenerationService aiQuestionGenerationService;

    @InjectMocks
    private PracticeQuestionServiceImpl service;

    private User mockUser;
    private TechnologyMaster mockTech;
    private ExperienceMaster mockExp;
    private PracticeSession mockSession;
    private PracticeQuestion mockQuestion;
    private String email;
    private UUID sessionId;
    private UUID questionId;

    @BeforeEach
    void setUp() {
        email = "test@user.com";
        sessionId = UUID.randomUUID();
        questionId = UUID.randomUUID();

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .name("Test User")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        mockTech = TechnologyMaster.builder()
                .id(UUID.randomUUID())
                .technologyName("Java")
                .isActive(true)
                .build();

        mockExp = ExperienceMaster.builder()
                .id(UUID.randomUUID())
                .experienceLabel("Mid")
                .isActive(true)
                .build();

        mockSession = PracticeSession.builder()
                .id(sessionId)
                .user(mockUser)
                .technology(mockTech)
                .experience(mockExp)
                .sessionStatus(SessionStatus.CREATED)
                .totalQuestions(1)
                .completedQuestions(0)
                .averageScore(0.0)
                .startedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        mockQuestion = PracticeQuestion.builder()
                .id(questionId)
                .practiceSession(mockSession)
                .questionNumber(1)
                .question("What is a class?")
                .questionStatus(QuestionStatus.PENDING)
                .isActive(true)
                .build();
    }

    @Test
    void generateAndSaveQuestions_Success() {
        AIQuestionResponse aiResp = new AIQuestionResponse(1, "What is a class?");
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(Collections.emptyList());
        when(aiQuestionGenerationService.generateQuestions(any(AIQuestionRequest.class)))
                .thenReturn(List.of(aiResp));
        when(practiceQuestionRepository.saveAll(anyList())).thenReturn(List.of(mockQuestion));

        List<PracticeQuestionResponse> responses = service.generateAndSaveQuestions(email, sessionId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(SessionStatus.IN_PROGRESS, mockSession.getSessionStatus());
        verify(practiceSessionRepository, times(1)).save(mockSession);
        verify(practiceQuestionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generateAndSaveQuestions_SessionNotInCreatedStatus_ThrowsException() {
        mockSession.setSessionStatus(SessionStatus.IN_PROGRESS);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

        assertThrows(InvalidOperationException.class, () -> service.generateAndSaveQuestions(email, sessionId));
    }

    @Test
    void generateAndSaveQuestions_AlreadyGenerated_ThrowsException() {
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(List.of(mockQuestion));

        assertThrows(InvalidOperationException.class, () -> service.generateAndSaveQuestions(email, sessionId));
    }

    @Test
    void generateAndSaveQuestions_Unauthorized_ThrowsException() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .email("other@test.com")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));

        assertThrows(UnauthorizedException.class, () -> service.generateAndSaveQuestions("other@test.com", sessionId));
    }

    @Test
    void getQuestionsForSession_Success() {
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(List.of(mockQuestion));

        List<PracticeQuestionResponse> responses = service.getQuestionsForSession(email, sessionId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getQuestionByNumber_Success() {
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(List.of(mockQuestion));

        PracticeQuestionResponse response = service.getQuestionByNumber(email, sessionId, 1);

        assertNotNull(response);
        assertEquals("What is a class?", response.question());
    }

    @Test
    void getQuestionByNumber_NotFound_ThrowsException() {
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
                .thenReturn(List.of(mockQuestion));

        assertThrows(ResourceNotFoundException.class, () -> service.getQuestionByNumber(email, sessionId, 2));
    }
}
