package com.interviewbridge.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for validating user registration requests.
 */
public record RegisterRequest(
    @NotBlank(message = EntityConstants.User.MSG_NAME_BLANK)
    @Size(max = EntityConstants.User.NAME_MAX_LENGTH, message = EntityConstants.User.MSG_NAME_SIZE)
    String name,

    @NotBlank(message = EntityConstants.User.MSG_EMAIL_BLANK)
    @Email(message = EntityConstants.User.MSG_EMAIL_INVALID)
    @Size(max = EntityConstants.User.EMAIL_MAX_LENGTH, message = EntityConstants.User.MSG_EMAIL_SIZE)
    String email,

    @NotBlank(message = EntityConstants.User.MSG_PASSWORD_BLANK)
    @Size(min = EntityConstants.User.PASSWORD_MIN_LENGTH, max = EntityConstants.User.PASSWORD_MAX_LENGTH, message = EntityConstants.User.MSG_PASSWORD_SIZE)
    String password
) {}
