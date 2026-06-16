package com.interviewbridge.serviceImpl;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.config.JwtService;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.AccountPendingApprovalException;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.InvalidCredentialsException;
import com.interviewbridge.request.LoginRequest;
import com.interviewbridge.request.RegisterRequest;
import com.interviewbridge.response.AuthResponse;
import com.interviewbridge.repository.UserRepository;
import com.interviewbridge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AuthService defining user registration and authentication business rules.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email() != null ? request.email().trim().toLowerCase() : null;
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException(SecurityConstants.MSG_EMAIL_EXISTS + normalizedEmail);
        }

        String normalizedPhone = request.phoneNumber() != null ? request.phoneNumber().trim() : null;
        if (normalizedPhone != null && !normalizedPhone.isEmpty()) {
            if (userRepository.existsByPhoneNumber(normalizedPhone)) {
                throw new DuplicateResourceException(SecurityConstants.MSG_PHONE_EXISTS + normalizedPhone);
            }
        }

        Boolean termsAccepted = request.termsAccepted();
        java.time.LocalDateTime termsAcceptedAt = null;
        if (Boolean.TRUE.equals(termsAccepted)) {
            termsAcceptedAt = java.time.LocalDateTime.now();
        }

        User user = User.builder()
            .name(request.name())
            .email(normalizedEmail)
            .phoneNumber(normalizedPhone)
            .password(passwordEncoder.encode(request.password()))
            .role(Role.ROLE_USER)
            .approvalStatus(ApprovalStatus.PENDING)
            .isActive(true)
            .termsAccepted(termsAccepted)
            .termsAcceptedAt(termsAcceptedAt)
            .build();

        User savedUser = userRepository.save(user);

        return new AuthResponse(
            null, // No token generated until approved
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getRole(),
            savedUser.getApprovalStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.email() != null ? request.email().trim() : "";
        User user;
        if (identifier.matches(SecurityConstants.EMAIL_PATTERN)) {
            String normalizedEmail = identifier.toLowerCase();
            user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException(SecurityConstants.MSG_INVALID_CREDENTIALS));
        } else {
            String normalizedPhone = identifier; // already trimmed
            user = userRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new InvalidCredentialsException(SecurityConstants.MSG_INVALID_CREDENTIALS));
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(SecurityConstants.MSG_INVALID_CREDENTIALS);
        }

        if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
            throw new AccountPendingApprovalException(SecurityConstants.MSG_PENDING_APPROVAL);
        }

        if (user.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new InvalidCredentialsException(SecurityConstants.MSG_REJECTED_ACCOUNT);
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException(SecurityConstants.MSG_INACTIVE_ACCOUNT);
        }

        // Generate JWT token
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.getIsActive(),
            true,
            true,
            true,
            List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(SecurityConstants.CLAIM_ROLE, user.getRole().name());
        extraClaims.put(SecurityConstants.CLAIM_STATUS, user.getApprovalStatus().name());

        String token = jwtService.generateToken(extraClaims, userDetails);

        return new AuthResponse(
            token,
            user.getEmail(),
            user.getName(),
            user.getRole(),
            user.getApprovalStatus()
        );
    }
}
