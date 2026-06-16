package com.interviewbridge.config;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.User;
import com.interviewbridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapRunnerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private AdminBootstrapRunner adminBootstrapRunner;

    private static final String DEFAULT_PASSWORD = "Admin@123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminBootstrapRunner, "defaultAdminPassword", DEFAULT_PASSWORD);
    }

    @Test
    void run_WhenAdminExists_ShouldNotBootstrap() throws Exception {
        // Arrange
        when(userRepository.existsByRole(Role.ROLE_ADMIN)).thenReturn(true);

        // Act
        adminBootstrapRunner.run(applicationArguments);

        // Assert
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void run_WhenAdminDoesNotExist_ShouldBootstrapAdmin() throws Exception {
        // Arrange
        when(userRepository.existsByRole(Role.ROLE_ADMIN)).thenReturn(false);
        when(passwordEncoder.encode(DEFAULT_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        // Act
        adminBootstrapRunner.run(applicationArguments);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals(SecurityConstants.ADMIN_BOOTSTRAP_NAME, savedUser.getName());
        assertEquals(SecurityConstants.ADMIN_BOOTSTRAP_EMAIL, savedUser.getEmail());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertEquals(Role.ROLE_ADMIN, savedUser.getRole());
        assertEquals(ApprovalStatus.APPROVED, savedUser.getApprovalStatus());
        assertTrue(savedUser.getIsActive());
        assertEquals(SecurityConstants.ADMIN_BOOTSTRAP_CREATED_BY, savedUser.getCreatedBy());
        assertEquals(SecurityConstants.ADMIN_BOOTSTRAP_CREATED_BY, savedUser.getUpdatedBy());
    }
}
