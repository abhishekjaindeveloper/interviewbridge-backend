package com.interviewbridge.config;

import com.interviewbridge.Enum.ApprovalStatus;
import com.interviewbridge.Enum.Role;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.entity.User;
import com.interviewbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Startup initialization component to bootstrap the default admin account if none exists.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value(SecurityConstants.ADMIN_DEFAULT_PASSWORD_PROP)
    private String defaultAdminPassword;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.existsByRole(Role.ROLE_ADMIN)) {
            log.info(SecurityConstants.MSG_ADMIN_BOOTSTRAP_EXISTS);
            userRepository.findByEmail(SecurityConstants.ADMIN_BOOTSTRAP_EMAIL).ifPresent(admin -> {
                if (admin.getPhoneNumber() == null || admin.getPhoneNumber().isEmpty()) {
                    admin.setPhoneNumber(SecurityConstants.ADMIN_BOOTSTRAP_PHONE);
                    userRepository.save(admin);
                    log.info("Populated phone number for the existing admin user.");
                }
            });
            return;
        }

        User defaultAdmin = User.builder()
                .name(SecurityConstants.ADMIN_BOOTSTRAP_NAME)
                .email(SecurityConstants.ADMIN_BOOTSTRAP_EMAIL)
                .phoneNumber(SecurityConstants.ADMIN_BOOTSTRAP_PHONE)
                .password(passwordEncoder.encode(defaultAdminPassword))
                .role(Role.ROLE_ADMIN)
                .approvalStatus(ApprovalStatus.APPROVED)
                .isActive(true)
                .termsAccepted(true)
                .termsAcceptedAt(java.time.LocalDateTime.now())
                .createdBy(SecurityConstants.ADMIN_BOOTSTRAP_CREATED_BY)
                .updatedBy(SecurityConstants.ADMIN_BOOTSTRAP_CREATED_BY)
                .build();

        userRepository.save(defaultAdmin);
        log.info(SecurityConstants.MSG_ADMIN_BOOTSTRAP_SUCCESS, SecurityConstants.ADMIN_BOOTSTRAP_EMAIL);
    }
}
