package com.interviewbridge.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO for validating user profile setup/update requests.
 */
public record UserProfileSetupRequest(
    @NotNull(message = EntityConstants.User.MSG_TECH_ID_REQUIRED)
    UUID technologyId,

    @NotNull(message = EntityConstants.User.MSG_EXP_ID_REQUIRED)
    UUID experienceId
) {}
