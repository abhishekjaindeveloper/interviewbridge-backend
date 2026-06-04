package com.interviewbridge.service;

import com.interviewbridge.request.LoginRequest;
import com.interviewbridge.request.RegisterRequest;
import com.interviewbridge.response.AuthResponse;

/**
 * Service interface defining user registration and authentication business rules.
 */
public interface AuthService {

    /**
     * Registers a new user in the system with pending status.
     *
     * @param request the registration details
     * @return the authentication response containing user info
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user and generates a JWT token if approved.
     *
     * @param request the credentials
     * @return the authentication response with JWT token
     */
    AuthResponse login(LoginRequest request);
}
