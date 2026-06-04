package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.response.AdminUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User mockUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .name("Pending User")
                .email("pending@user.com")
                .role(Role.ROLE_USER)
                .approvalStatus(ApprovalStatus.PENDING)
                .isActive(true)
                .build();
    }

    @Test
    void getPendingUsers_Success() {
        when(userRepository.findByApprovalStatus(ApprovalStatus.PENDING))
                .thenReturn(List.of(mockUser));

        List<AdminUserResponse> responses = adminService.getPendingUsers();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        AdminUserResponse response = responses.get(0);
        assertEquals(userId, response.id());
        assertEquals("Pending User", response.name());
        assertEquals("pending@user.com", response.email());
        assertEquals(Role.ROLE_USER, response.role());
        assertEquals(ApprovalStatus.PENDING, response.approvalStatus());
    }

    @Test
    void approveUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        adminService.approveUser(userId);

        assertEquals(ApprovalStatus.APPROVED, mockUser.getApprovalStatus());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void approveUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.approveUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void approveUser_AlreadyApproved_ThrowsException() {
        mockUser.setApprovalStatus(ApprovalStatus.APPROVED);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        assertThrows(InvalidOperationException.class, () -> adminService.approveUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void rejectUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        adminService.rejectUser(userId);

        assertEquals(ApprovalStatus.REJECTED, mockUser.getApprovalStatus());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void rejectUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.rejectUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void rejectUser_AlreadyRejected_ThrowsException() {
        mockUser.setApprovalStatus(ApprovalStatus.REJECTED);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        assertThrows(InvalidOperationException.class, () -> adminService.rejectUser(userId));
        verify(userRepository, never()).save(any(User.class));
    }
}
