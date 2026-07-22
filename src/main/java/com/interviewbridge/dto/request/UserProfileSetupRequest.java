package com.interviewbridge.dto.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO for validating user profile setup/update requests.
 */
public record UserProfileSetupRequest(
    UUID technologyId,

    UUID experienceId,

    @Size(max = EntityConstants.User.NAME_MAX_LENGTH, message = EntityConstants.User.MSG_NAME_SIZE)
    String name
) {}
