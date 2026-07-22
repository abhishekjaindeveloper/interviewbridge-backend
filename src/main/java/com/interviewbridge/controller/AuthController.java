package com.interviewbridge.controller;

import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.LoginRequest;
import com.interviewbridge.dto.request.RegisterRequest;
import com.interviewbridge.dto.response.ApiResponse;
import com.interviewbridge.dto.response.AuthResponse;
import com.interviewbridge.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller exposing authentication APIs.
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user registration.
     *
     * @param request the registration details
     * @return the response entity containing custom ApiResponse wrapping user registration info
     */
    @PostMapping(SecurityConstants.REGISTER_URL)
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_USER_REGISTERED_SUCCESS,
            response
        ));
    }

    /**
     * Endpoint for user login.
     *
     * @param request the login credentials
     * @return the response entity containing custom ApiResponse wrapping JWT token info
     */
    @PostMapping(SecurityConstants.LOGIN_URL)
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(
            true,
            SecurityConstants.MSG_LOGIN_SUCCESS,
            response
        ));
    }
}

