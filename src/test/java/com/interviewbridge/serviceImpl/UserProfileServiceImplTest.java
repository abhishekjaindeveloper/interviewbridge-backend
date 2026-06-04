package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.Role;
import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.ExperienceMasterRepository;
import com.interviewbridge.repository.TechnologyMasterRepository;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.request.UserProfileSetupRequest;
import com.interviewbridge.response.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TechnologyMasterRepository technologyRepository;

    @Mock
    private ExperienceMasterRepository experienceRepository;

    @InjectMocks
    private UserProfileServiceImpl service;

    private User mockUser;
    private TechnologyMaster mockTech;
    private ExperienceMaster mockExp;
    private String email;
    private UUID techId;
    private UUID expId;

    @BeforeEach
    void setUp() {
        email = "test@user.com";
        techId = UUID.randomUUID();
        expId = UUID.randomUUID();

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
    }

    @Test
    void setupProfile_Success() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(technologyRepository.findById(techId)).thenReturn(Optional.of(mockTech));
        when(experienceRepository.findById(expId)).thenReturn(Optional.of(mockExp));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        UserProfileResponse response = service.setupProfile(email, request);

        assertNotNull(response);
        assertEquals(techId, response.technology().id());
        assertEquals(expId, response.experience().id());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void setupProfile_UserNotFound_ThrowsException() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.setupProfile(email, request));
    }

    @Test
    void setupProfile_AlreadySetup_ThrowsException() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        mockUser.setTechnology(mockTech);
        mockUser.setExperience(mockExp);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));

        assertThrows(DuplicateResourceException.class, () -> service.setupProfile(email, request));
    }

    @Test
    void setupProfile_TechnologyInactive_ThrowsException() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        mockTech.setIsActive(false);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(technologyRepository.findById(techId)).thenReturn(Optional.of(mockTech));

        assertThrows(ResourceNotFoundException.class, () -> service.setupProfile(email, request));
    }

    @Test
    void setupProfile_ExperienceInactive_ThrowsException() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        mockExp.setIsActive(false);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(technologyRepository.findById(techId)).thenReturn(Optional.of(mockTech));
        when(experienceRepository.findById(expId)).thenReturn(Optional.of(mockExp));

        assertThrows(ResourceNotFoundException.class, () -> service.setupProfile(email, request));
    }

    @Test
    void updateProfile_Success() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        mockUser.setTechnology(mockTech);
        mockUser.setExperience(mockExp);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(technologyRepository.findById(techId)).thenReturn(Optional.of(mockTech));
        when(experienceRepository.findById(expId)).thenReturn(Optional.of(mockExp));
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        UserProfileResponse response = service.updateProfile(email, request);

        assertNotNull(response);
        assertEquals(techId, response.technology().id());
        assertEquals(expId, response.experience().id());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void updateProfile_NotSetupYet_ThrowsException() {
        UserProfileSetupRequest request = new UserProfileSetupRequest(techId, expId);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));

        assertThrows(ResourceNotFoundException.class, () -> service.updateProfile(email, request));
    }

    @Test
    void getProfile_Success() {
        mockUser.setTechnology(mockTech);
        mockUser.setExperience(mockExp);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));

        UserProfileResponse response = service.getProfile(email);

        assertNotNull(response);
        assertEquals(techId, response.technology().id());
        assertEquals(expId, response.experience().id());
    }

    @Test
    void getProfile_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getProfile(email));
    }
}
