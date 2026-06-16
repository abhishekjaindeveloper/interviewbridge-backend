package com.interviewbridge.constants;

/**
 * Utility class containing all security-related constants, endpoints, roles,
 * header names, claim names, and exception messages to avoid hardcoded values.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    // HTTP Headers and Prefixes
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // JWT Claims
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_STATUS = "approvalStatus";

    // API Ant Matchers and Endpoints
    public static final String AUTH_API_ANT_MATCHER = "/api/auth/**";
    public static final String ADMIN_API_ANT_MATCHER = "/api/admin/**";
    public static final String USER_API_ANT_MATCHER = "/api/user/**";
    
    // Auth Endpoint Paths
    public static final String REGISTER_URL = "/api/auth/register";
    public static final String LOGIN_URL = "/api/auth/login";

    // Admin Endpoint Paths
    public static final String ADMIN_USERS_PENDING_URL = "/api/admin/users/pending";
    public static final String ADMIN_USERS_APPROVE_URL = "/api/admin/users/{id}/approve";
    public static final String ADMIN_USERS_REJECT_URL = "/api/admin/users/{id}/reject";
    public static final String PATH_VAR_ID = "id";

    // Roles and Authorities
    public static final String ROLE_ADMIN_NAME = "ADMIN";
    public static final String ROLE_USER_NAME = "USER";

    // Authority expressions used in @PreAuthorize
    public static final String ADMIN_ACCESS = "hasRole('" + ROLE_ADMIN_NAME + "')";
    public static final String USER_OR_ADMIN_ACCESS = "hasAnyRole('" + ROLE_USER_NAME + "', '" + ROLE_ADMIN_NAME + "')";
    
    // Config properties keys (for @Value or logging)
    public static final String JWT_SECRET_PROP = SecurityConstants.JWT_SECRET_RAW_PROP;
    private static final String JWT_SECRET_RAW_PROP = "${app.jwt.secret}";
    public static final String JWT_EXPIRATION_PROP = "${app.jwt.expiration-ms}";
    public static final String CORS_ALLOWED_ORIGINS_PROP = "${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080,http://localhost:5000,http://localhost:9000}";

    // Error Messages
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";
    public static final String MSG_ACCESS_DENIED = "Access Denied";
    public static final String MSG_INVALID_TOKEN = "Invalid JWT token";
    public static final String MSG_EXPIRED_TOKEN = "Expired JWT token";
    public static final String MSG_USER_NOT_FOUND = "User not found with email: ";
    public static final String MSG_USER_NOT_FOUND_ID = "User not found with id: ";
    public static final String MSG_EMAIL_EXISTS = "User with this email already exists: ";
    public static final String MSG_PENDING_APPROVAL = "Your account is pending admin approval.";
    public static final String MSG_REJECTED_ACCOUNT = "Your account registration was rejected by the admin.";
    public static final String MSG_INVALID_CREDENTIALS = "Invalid email or password.";
    public static final String MSG_USER_APPROVED_SUCCESS = "User approved successfully.";
    public static final String MSG_USER_REJECTED_SUCCESS = "User registration rejected successfully.";
    public static final String MSG_USER_REGISTERED_SUCCESS = "User registered successfully, pending admin approval.";
    public static final String MSG_LOGIN_SUCCESS = "Login successful.";
    public static final String MSG_INVALID_APPROVAL_STATUS = "Only users in PENDING status can be approved or rejected.";
    
    // Technology Endpoint Paths
    public static final String ADMIN_TECH_BASE_URL = "/api/admin/technologies";
    public static final String ADMIN_TECH_ID_URL = "/api/admin/technologies/{id}";
    public static final String ADMIN_TECH_ACTIVATE_URL = "/api/admin/technologies/{id}/activate";
    public static final String ADMIN_TECH_DEACTIVATE_URL = "/api/admin/technologies/{id}/deactivate";
    public static final String USER_TECH_BASE_URL = "/api/user/technologies";

    // Technology Messages
    public static final String MSG_TECH_CREATED_SUCCESS = "Technology created successfully.";
    public static final String MSG_TECH_UPDATED_SUCCESS = "Technology updated successfully.";
    public static final String MSG_TECH_ACTIVATED_SUCCESS = "Technology activated successfully.";
    public static final String MSG_TECH_DEACTIVATED_SUCCESS = "Technology deactivated successfully.";
    public static final String MSG_TECH_NOT_FOUND = "Technology not found with id: ";
    public static final String MSG_TECH_NAME_EXISTS = "Technology name already exists: ";
    
    // Experience Endpoint Paths
    public static final String ADMIN_EXP_BASE_URL = "/api/admin/experiences";
    public static final String ADMIN_EXP_ID_URL = "/api/admin/experiences/{id}";
    public static final String ADMIN_EXP_ACTIVATE_URL = "/api/admin/experiences/{id}/activate";
    public static final String ADMIN_EXP_DEACTIVATE_URL = "/api/admin/experiences/{id}/deactivate";
    public static final String USER_EXP_BASE_URL = "/api/user/experiences";

    // Experience Messages
    public static final String MSG_EXP_CREATED_SUCCESS = "Experience created successfully.";
    public static final String MSG_EXP_UPDATED_SUCCESS = "Experience updated successfully.";
    public static final String MSG_EXP_ACTIVATED_SUCCESS = "Experience activated successfully.";
    public static final String MSG_EXP_DEACTIVATED_SUCCESS = "Experience deactivated successfully.";
    public static final String MSG_EXP_NOT_FOUND = "Experience not found with id: ";
    public static final String MSG_EXP_LABEL_EXISTS = "Experience label already exists: ";

    // User Profile Endpoint Paths
    public static final String USER_PROFILE_SETUP_URL = "/api/user/profile/setup";
    public static final String USER_PROFILE_URL = "/api/user/profile";

    // User Profile Messages
    public static final String MSG_PROFILE_SETUP_SUCCESS = "User profile set up successfully.";
    public static final String MSG_PROFILE_UPDATED_SUCCESS = "User profile updated successfully.";
    public static final String MSG_PROFILE_ALREADY_SETUP = "User profile is already set up. Use PUT /api/user/profile to update.";
    public static final String MSG_PROFILE_NOT_SETUP = "User profile has not been set up yet. Use POST /api/user/profile/setup.";

    // Practice Session Endpoint Paths
    public static final String USER_PRACTICE_SESSIONS_URL = "/api/user/practice-sessions";
    public static final String USER_PRACTICE_SESSIONS_ID_URL = "/api/user/practice-sessions/{id}";

    // Practice Session Messages
    public static final String MSG_SESSION_STARTED_SUCCESS = "Practice session started successfully.";
    public static final String MSG_SESSION_NOT_FOUND = "Practice session not found with id: ";
    public static final String MSG_SESSION_ACCESS_DENIED = "You do not have permission to access this practice session.";

    // Question Generation Endpoint Paths and Path Variables
    public static final String USER_SESSION_QUESTIONS_GENERATE_URL = "/api/user/practice-sessions/{sessionId}/questions/generate";
    public static final String USER_SESSION_QUESTIONS_URL = "/api/user/practice-sessions/{sessionId}/questions";
    public static final String USER_SESSION_QUESTIONS_NUM_URL = "/api/user/practice-sessions/{sessionId}/questions/{questionNumber}";
    public static final String PATH_VAR_SESSION_ID = "sessionId";
    public static final String PATH_VAR_QUESTION_NUMBER = "questionNumber";

    // Question Generation Messages
    public static final String MSG_QUESTIONS_GENERATED_SUCCESS = "Questions generated successfully.";
    public static final String MSG_QUESTIONS_ALREADY_GENERATED = "Questions have already been generated for this practice session.";
    public static final String MSG_QUESTION_NOT_FOUND = "Question not found with number: ";

    // Practice Answer Endpoint Paths and Path Variables
    public static final String USER_PRACTICE_QUESTIONS_ID_URL = "/api/user/practice-questions/{questionId}";
    public static final String USER_PRACTICE_QUESTIONS_ANSWER_URL = "/api/user/practice-questions/{questionId}/answer";
    public static final String PATH_VAR_QUESTION_ID = "questionId";

    // Practice Answer Messages
    public static final String MSG_ANSWER_SUBMITTED_SUCCESS = "Answer submitted successfully.";
    public static final String MSG_QUESTION_NOT_FOUND_ID = "Question not found with id: ";
    public static final String MSG_SESSION_NOT_IN_PROGRESS = "Practice session must be IN_PROGRESS.";
    public static final String MSG_QUESTION_ALREADY_ANSWERED = "Question has already been answered.";
    public static final String MSG_CONCURRENCY_CONFLICT = "A concurrency conflict occurred. The session has been updated by another request. Please try again.";

    // Practice Evaluation Endpoint Paths
    public static final String USER_PRACTICE_QUESTIONS_EVALUATE_URL = "/api/user/practice-questions/{questionId}/evaluate";
    public static final String USER_PRACTICE_QUESTIONS_EVALUATION_URL = "/api/user/practice-questions/{questionId}/evaluation";

    // Practice Evaluation Messages
    public static final String MSG_EVALUATION_SUCCESS = "Question evaluated successfully.";
    public static final String MSG_QUESTION_NOT_ANSWERED = "Question must be answered before it can be evaluated.";
    public static final String MSG_EVALUATION_NOT_FOUND = "Evaluation results not found for this question.";
    public static final String MSG_QUESTION_ALREADY_EVALUATED = "Question has already been evaluated.";
    public static final String MSG_INVALID_SCORE_RANGE = "Evaluation score must be between 0 and 10.";

    // Exception validation messages
    public static final String MSG_VALIDATION_FAILED = "Validation failed";
    public static final String MSG_INTERNAL_SERVER_ERROR = "An unexpected error occurred";

    // Remediation business validation messages
    public static final String MSG_QUESTIONS_GENERATED_CREATED_ONLY = "Questions can only be generated for sessions in CREATED status.";
    public static final String MSG_AI_EVALUATION_FAILED = "AI evaluation service processing failed: ";

    // JWT Secret Validation Messages
    public static final String MSG_JWT_SECRET_REQUIRED = "JWT secret key must not be null or empty.";
    public static final String MSG_JWT_SECRET_INSUFFICIENT_LENGTH = "JWT secret key must be at least 256 bits (32 bytes) long when decoded from Base64 for HS256 algorithm.";
    public static final String MSG_JWT_SECRET_INVALID_BASE64 = "JWT secret key is not a valid Base64 encoded string.";

    // Default Admin Bootstrap constants
    public static final String ADMIN_DEFAULT_PASSWORD_PROP = "${app.admin.default-password}";
    public static final String ADMIN_BOOTSTRAP_NAME = "Abhishek Jain";
    public static final String ADMIN_BOOTSTRAP_EMAIL = "abhishek@gmail.com";
    public static final String ADMIN_BOOTSTRAP_PHONE = "9174686803";
    public static final String ADMIN_BOOTSTRAP_CREATED_BY = "SYSTEM_BOOTSTRAP";
    public static final String MSG_ADMIN_BOOTSTRAP_SUCCESS = "Default admin account bootstrapped successfully: {}";
    public static final String MSG_ADMIN_BOOTSTRAP_EXISTS = "Admin account already exists. Bootstrapping skipped.";
    public static final String MSG_INACTIVE_ACCOUNT = "Your account is currently inactive.";
    
    // Pattern and Unique Validation Constants
    public static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    public static final String MSG_PHONE_EXISTS = "User with this phone number already exists: ";
}
