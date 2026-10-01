package com.interviewbridge.service;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.AIQuestionRequest;
import com.interviewbridge.dto.response.AIQuestionResponse;
import com.interviewbridge.dto.response.PracticeQuestionResponse;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.PracticeQuestion;
import com.interviewbridge.entity.PracticeSession;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.enums.Role;
import com.interviewbridge.enums.SessionStatus;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.repository.PracticeQuestionRepository;
import com.interviewbridge.repository.PracticeSessionRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.service.impl.PracticeQuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private PracticeQuestionServiceImpl practiceQuestionService;

    private User testUser;
    private PracticeSession testSession;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();

        testUser = User.builder()
            .email("test@example.com")
            .role(Role.ROLE_USER)
            .build();

        TechnologyMaster tech = TechnologyMaster.builder()
            .technologyName("Java")
            .build();

        ExperienceMaster exp = ExperienceMaster.builder()
            .experienceLabel("Mid-Level")
            .build();

        testSession = PracticeSession.builder()
            .id(sessionId)
            .user(testUser)
            .technology(tech)
            .experience(exp)
            .totalQuestions(2)
            .sessionStatus(SessionStatus.CREATED)
            .build();
    }

    @Test
    @DisplayName("Successfully generate and persist AI questions for a practice session")
    void generateAndSaveQuestions_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
            .thenReturn(Collections.emptyList());

        List<AIQuestionResponse> mockAiResponses = List.of(
            new AIQuestionResponse(1, "What is the Java memory model?"),
            new AIQuestionResponse(2, "How do you avoid deadlock in multi-threaded Java?")
        );

        when(aiQuestionGenerationService.generateQuestions(any(AIQuestionRequest.class)))
            .thenReturn(mockAiResponses);

        List<PracticeQuestion> mockSaved = List.of(
            PracticeQuestion.builder().id(UUID.randomUUID()).practiceSession(testSession).questionNumber(1).question("What is the Java memory model?").build(),
            PracticeQuestion.builder().id(UUID.randomUUID()).practiceSession(testSession).questionNumber(2).question("How do you avoid deadlock in multi-threaded Java?").build()
        );
        when(practiceQuestionRepository.saveAll(anyList())).thenReturn(mockSaved);

        List<PracticeQuestionResponse> result = practiceQuestionService.generateAndSaveQuestions("test@example.com", sessionId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(SessionStatus.IN_PROGRESS, testSession.getSessionStatus());
        verify(practiceSessionRepository).save(testSession);
        verify(practiceQuestionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("AI generation failure propagates and preserves session state")
    void generateAndSaveQuestions_aiFailure_propagatesException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(practiceSessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(practiceQuestionRepository.findByPracticeSessionIdOrderByQuestionNumberAsc(sessionId))
            .thenReturn(Collections.emptyList());

        when(aiQuestionGenerationService.generateQuestions(any(AIQuestionRequest.class)))
            .thenThrow(new InvalidOperationException(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED));

        InvalidOperationException ex = assertThrows(
            InvalidOperationException.class,
            () -> practiceQuestionService.generateAndSaveQuestions("test@example.com", sessionId)
        );

        assertEquals(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED, ex.getMessage());
        assertEquals(SessionStatus.CREATED, testSession.getSessionStatus());
        verify(practiceQuestionRepository, never()).saveAll(anyList());
    }
}
