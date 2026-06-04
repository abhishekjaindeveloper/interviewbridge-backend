package com.interviewbridge.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for validating experience master update requests.
 */
public record ExperienceUpdateRequest(
    @NotBlank(message = EntityConstants.Experience.MSG_EXP_LABEL_BLANK)
    @Size(max = EntityConstants.Experience.EXP_LABEL_MAX_LENGTH, message = EntityConstants.Experience.MSG_EXP_LABEL_SIZE)
    String experienceLabel
) {}
