package com.interviewbridge.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for validating technology creation requests.
 */
public record TechnologyCreateRequest(
    @NotBlank(message = EntityConstants.Technology.MSG_TECH_NAME_BLANK)
    @Size(max = EntityConstants.Technology.TECH_NAME_MAX_LENGTH, message = EntityConstants.Technology.MSG_TECH_NAME_SIZE)
    String technologyName,

    @Size(max = EntityConstants.Technology.DESC_MAX_LENGTH, message = EntityConstants.Technology.MSG_DESC_SIZE)
    String description
) {}
