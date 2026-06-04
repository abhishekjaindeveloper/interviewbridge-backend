package com.interviewbridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * JPA Auditing Configuration for tracking entity creation and modification.
 */
@Configuration
public class AuditingConfig {

    private static final String DEFAULT_AUDITOR = "SYSTEM";
    private static final String ANONYMOUS_USER = "anonymousUser";

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context == null) {
                return Optional.of(DEFAULT_AUDITOR);
            }
            Authentication authentication = context.getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || ANONYMOUS_USER.equals(authentication.getPrincipal())) {
                return Optional.of(DEFAULT_AUDITOR);
            }
            return Optional.ofNullable(authentication.getName());
        };
    }
}
