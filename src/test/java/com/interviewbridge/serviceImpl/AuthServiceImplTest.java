package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.config.JwtService;
//import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.AccountPendingApprovalException;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.InvalidCredentialsException;
import com.interviewbridge.request.LoginRequest;
import com.interviewbridge.request.RegisterRequest;
import com.interviewbridge.response.AuthResponse;
import com.interviewbridge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private String email;

    @BeforeEach
    void setUp() {
        email = "test@user.com";
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email(email)
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .approvalStatus(ApprovalStatus.APPROVED)
                .isActive(true)
                .build();
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest("Test User", email, "9876543210", true, "password123");
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        User pendingUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email(email)
                .role(Role.ROLE_USER)
                .approvalStatus(ApprovalStatus.PENDING)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(pendingUser);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("Test User", response.name());
        assertEquals(Role.ROLE_USER, response.role());
        assertEquals(ApprovalStatus.PENDING, response.approvalStatus());
        assertNull(response.token());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_WithPhoneNumber_Success() {
        String phone = "9174686803";
        RegisterRequest request = new RegisterRequest("Test User", email, phone, true, "password123");
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(phone)).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        User pendingUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email(email)
                .phoneNumber(phone)
                .role(Role.ROLE_USER)
                .approvalStatus(ApprovalStatus.PENDING)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(pendingUser);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(email, response.email());
        assertEquals("Test User", response.name());
        assertEquals(Role.ROLE_USER, response.role());
        assertEquals(ApprovalStatus.PENDING, response.approvalStatus());
        assertNull(response.token());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest("Test User", email, "9876543210", true, "password123");
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_DuplicatePhoneNumber_ThrowsException() {
        String phone = "9174686803";
        RegisterRequest request = new RegisterRequest("Test User", email, phone, true, "password123");
        when(userRepository.existsByEmail(email.toLowerCase())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(phone)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest(email, "password123");
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(any(), any(UserDetails.class))).thenReturn("mockJwtToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.token());
        assertEquals(email, response.email());
        assertEquals("Test User", response.name());
        assertEquals(Role.ROLE_USER, response.role());
        assertEquals(ApprovalStatus.APPROVED, response.approvalStatus());
    }

    @Test
    void login_PhoneSuccess() {
        String phone = "9174686803";
        mockUser.setPhoneNumber(phone);
        LoginRequest request = new LoginRequest(phone, "password123");
        when(userRepository.findByPhoneNumber(phone)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(any(), any(UserDetails.class))).thenReturn("mockJwtToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.token());
        assertEquals(email, response.email());
        assertEquals("Test User", response.name());
        assertEquals(Role.ROLE_USER, response.role());
        assertEquals(ApprovalStatus.APPROVED, response.approvalStatus());
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest(email, "password123");
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_PhoneUserNotFound_ThrowsException() {
        String phone = "9174686803";
        LoginRequest request = new LoginRequest(phone, "password123");
        when(userRepository.findByPhoneNumber(phone)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest(email, "wrongPassword");
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_PendingApproval_ThrowsException() {
        LoginRequest request = new LoginRequest(email, "password123");
        mockUser.setApprovalStatus(ApprovalStatus.PENDING);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        assertThrows(AccountPendingApprovalException.class, () -> authService.login(request));
    }

    @Test
    void login_RejectedAccount_ThrowsException() {
        LoginRequest request = new LoginRequest(email, "password123");
        mockUser.setApprovalStatus(ApprovalStatus.REJECTED);
        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
