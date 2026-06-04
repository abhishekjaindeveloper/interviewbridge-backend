package com.interviewbridge.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for validating user login requests.
 */
public record LoginRequest(
    @NotBlank(message = EntityConstants.User.MSG_EMAIL_BLANK)
    @Email(message = EntityConstants.User.MSG_EMAIL_INVALID)
    String email,

    @NotBlank(message = EntityConstants.User.MSG_PASSWORD_BLANK)
    String password
) {}
