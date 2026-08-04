package com.interviewbridge.dto.request;

import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.enums.WorkMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

/**
 * DTO for validating user profile setup/update requests.
 */
public record UserProfileSetupRequest(
    UUID technologyId,

    UUID experienceId,

    @Size(max = EntityConstants.User.NAME_MAX_LENGTH, message = EntityConstants.User.MSG_NAME_SIZE)
    String name,

    @Size(max = EntityConstants.User.JOB_ROLE_MAX_LENGTH, message = EntityConstants.User.MSG_JOB_ROLE_SIZE)
    String preferredJobRole,

    @Size(max = EntityConstants.User.LOCATION_MAX_LENGTH, message = EntityConstants.User.MSG_LOCATION_SIZE)
    String preferredLocation,

    WorkMode preferredWorkMode,

    @PositiveOrZero(message = EntityConstants.User.MSG_EXPECTED_SALARY_POSITIVE)
    Double expectedSalary,

    Boolean jobAlertEnabled
) {}
