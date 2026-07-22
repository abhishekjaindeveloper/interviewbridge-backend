package com.interviewbridge.service.impl;

import com.interviewbridge.enums.ApprovalStatus;
import com.interviewbridge.enums.Role;
import com.interviewbridge.security.JwtService;
import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.User;
import com.interviewbridge.exception.AccountPendingApprovalException;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.InvalidCredentialsException;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.exception.UserAccountRejectedException;
import com.interviewbridge.dto.request.LoginRequest;
import com.interviewbridge.dto.request.RegisterRequest;
import com.interviewbridge.dto.response.AuthResponse;
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
                throw new DuplicateResourceException(SecurityConstants.MSG_PHONE_ALREADY_REGISTERED);
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
            savedUser.getPhoneNumber(),
            savedUser.getRole(),
            savedUser.getApprovalStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String identifier = request.email() != null ? request.email().trim() : "";
        if (!identifier.isEmpty() && Character.isDigit(identifier.charAt(0))) {
            if (!identifier.matches("^[6-9][0-9]{9}$")) {
                throw new InvalidOperationException(EntityConstants.User.MSG_PHONE_NUMBER_INVALID);
            }
        }
        User user;
        if (identifier.matches(SecurityConstants.EMAIL_PATTERN)) {
            String normalizedEmail = identifier.toLowerCase();
            user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException(SecurityConstants.MSG_USER_NOT_FOUND_LOGIN));
        } else {
            String normalizedPhone = identifier; // already trimmed
            user = userRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new InvalidCredentialsException(SecurityConstants.MSG_USER_NOT_FOUND_LOGIN));
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(SecurityConstants.MSG_INCORRECT_PASSWORD);
        }

        if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
            throw new AccountPendingApprovalException(SecurityConstants.MSG_PENDING_APPROVAL);
        }

        if (user.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new UserAccountRejectedException(SecurityConstants.MSG_REJECTED_ACCOUNT, user.getRejectionReason());
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
            user.getPhoneNumber(),
            user.getRole(),
            user.getApprovalStatus()
        );
    }
}

