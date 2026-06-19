package com.interviewbridge.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for validating user rejection requests.
 */
public record UserRejectionRequest(
    @NotBlank(message = EntityConstants.User.MSG_REJECTION_REASON_BLANK)
    @Size(min = EntityConstants.User.REJECTION_REASON_MIN_LENGTH, max = EntityConstants.User.REJECTION_REASON_MAX_LENGTH, message = EntityConstants.User.MSG_REJECTION_REASON_SIZE)
    String reason
) {}
