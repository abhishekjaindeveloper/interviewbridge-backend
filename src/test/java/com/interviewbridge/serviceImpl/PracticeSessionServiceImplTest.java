package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.Role;
import com.interviewbridge.Enum.SessionStatus;
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
import com.interviewbridge.request.StartPracticeSessionRequest;
import com.interviewbridge.response.PracticeSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PracticeSessionServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechnologyMasterRepository technologyRepository;

    @Mock
    private ExperienceMasterRepository experienceRepository;

    @Mock
    private PracticeSessionRepository practiceSessionRepository;

    @InjectMocks
    private PracticeSessionServiceImpl service;

    private User mockUser;
    private TechnologyMaster mockTech;
    private ExperienceMaster mockExp;
    private PracticeSession mockSession;
    private String email;
    private UUID techId;
    private UUID expId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        email = "test@user.com";
        techId = UUID.randomUUID();
        expId = UUID.randomUUID();
        sessionId = UUID.randomUUID();

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .name("Test User")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        mockTech = TechnologyMaster.builder()
                .id(techId)
                .technologyName("Java")
                .isActive(true)
                .build();

        mockExp = ExperienceMaster.builder()
                .id(expId)
                .experienceLabel("Mid")
                .isActive(true)
                .build();

        mockSession = PracticeSession.builder()
                .id(sessionId)
                .user(mockUser)
                .technology(mockTech)
                .experience(mockExp)
                .sessionStatus(SessionStatus.CREATED)
                .totalQuestions(5)
                .completedQuestions(0)
                .averageScore(0.0)
                .startedAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }

    @Test
    void startSession_Success() {
        StartPracticeSessionRequest request = new StartPracticeSessionRequest(techId, expId, 5);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(technologyRepository.findById(techId)).thenReturn(Optional.of(mockTech));
        when(experienceRepository.findById(expId)).thenReturn(Optional.of(mockExp));
        when(practiceSessionRepository.save(any(PracticeSession.class))).thenReturn(mockSession);

        PracticeSessionResponse response = service.startSession(email, request);

        assertNotNull(response);
        assertEquals(sessionId, response.id());
        assertEquals(SessionStatus.CREATED, response.sessionStatus());
        verify(practiceSessionRepository, times(1)).save(any(PracticeSession.class));
    }

    @Test
    void startSession_UserNotFound_ThrowsException() {
        StartPracticeSessionRequest request = new StartPracticeSessionRequest(techId, expId, 5);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.startSession(email, request));
    }

    @Test
    void startSession_TechnologyInactive_ThrowsException() {
        StartPracticeSessionRequest request = new StartPracticeSessionRequest(techId, expId, 5);
        mockTech.setIsActive(false);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(technologyRepository.findById(techId)).thenReturn(Optional.of(mockTech));

        assertThrows(ResourceNotFoundException.class, () -> service.startSession(email, request));
    }

    @Test
    void getSessionById_Owner_Success() {
        when(practiceSessionRepository.findByIdWithUserAndTechnologyAndExperience(sessionId))
                .thenReturn(Optional.of(mockSession));
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));

        PracticeSessionResponse response = service.getSessionById(email, sessionId);

        assertNotNull(response);
        assertEquals(sessionId, response.id());
    }

    @Test
    void getSessionById_AdminBypass_Success() {
        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .isActive(true)
                .build();

        when(practiceSessionRepository.findByIdWithUserAndTechnologyAndExperience(sessionId))
                .thenReturn(Optional.of(mockSession));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));

        PracticeSessionResponse response = service.getSessionById("admin@test.com", sessionId);

        assertNotNull(response);
        assertEquals(sessionId, response.id());
    }

    @Test
    void getSessionById_Unauthorized_ThrowsException() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .email("other@test.com")
                .role(Role.ROLE_USER)
                .isActive(true)
                .build();

        when(practiceSessionRepository.findByIdWithUserAndTechnologyAndExperience(sessionId))
                .thenReturn(Optional.of(mockSession));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        assertThrows(UnauthorizedException.class, () -> service.getSessionById("other@test.com", sessionId));
    }

    @Test
    void getSessionById_NotFound_ThrowsException() {
        when(practiceSessionRepository.findByIdWithUserAndTechnologyAndExperience(sessionId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getSessionById(email, sessionId));
    }

    @Test
    void getSessionsForUser_Success() {
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(true);
        when(practiceSessionRepository.findByUserEmailWithUserAndTechnologyAndExperienceOrderByCreatedAtDesc(email.toLowerCase()))
                .thenReturn(List.of(mockSession));

        List<PracticeSessionResponse> responses = service.getSessionsForUser(email);

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getSessionsForUser_UserNotFound_ThrowsException() {
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.getSessionsForUser(email));
    }
}
