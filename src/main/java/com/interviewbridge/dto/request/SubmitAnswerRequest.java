package com.interviewbridge.dto.request;

import com.interviewbridge.constants.EntityConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for validating answer submission requests.
 */
public record SubmitAnswerRequest(
    @NotBlank(message = EntityConstants.PracticeQuestion.MSG_ANSWER_BLANK)
    @Size(max = EntityConstants.PracticeQuestion.ANSWER_MAX_LENGTH, message = EntityConstants.PracticeQuestion.MSG_ANSWER_SIZE)
    String answer
) {}
