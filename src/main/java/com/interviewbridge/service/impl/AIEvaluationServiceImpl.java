package com.interviewbridge.service.impl;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.interviewbridge.config.GeminiProperties;
import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.service.AIEvaluationService;
import com.interviewbridge.service.gemini.GeminiClientWrapper;
import com.interviewbridge.service.gemini.GeminiEvaluationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Production implementation of AIEvaluationService powered by Google's Gemini API.
 * Employs structured JSON schema enforcement, strict validation, and fair candidate scoring.
 */
@Service
@Profile("!mock")
@RequiredArgsConstructor
@Slf4j
public class AIEvaluationServiceImpl implements AIEvaluationService {

    private final GeminiProperties geminiProperties;
    private final GeminiClientWrapper geminiClientWrapper;
    private final ObjectMapper objectMapper;

    @Override
    public AIEvaluationResponse evaluateAnswer(AIEvaluationRequest request) {
        String tech = request.technology() != null ? request.technology().trim() : "Software Engineering";
        String exp = request.experience() != null ? request.experience().trim() : "Intermediate";
        String question = request.question() != null ? request.question().trim() : "";
        String userAnswer = request.userAnswer() != null ? request.userAnswer().trim() : "";
        String referenceAnswer = request.referenceAnswer() != null ? request.referenceAnswer().trim() : "";

        log.info("Initiating Gemini evaluation: technology='{}', experience='{}', model='{}'",
                tech, exp, geminiProperties.getModel());

        long startTime = System.currentTimeMillis();

        try {
            String prompt = buildPrompt(question, userAnswer, tech, exp, referenceAnswer);
            GenerateContentConfig config = buildContentConfig();

            String rawJson = geminiClientWrapper.generateContent(
                    geminiProperties.getModel(),
                    prompt,
                    config
            );

            AIEvaluationResponse response = parseAndValidateResponse(rawJson);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully evaluated answer with score={} for technology='{}' in {}ms",
                    response.score(), tech, duration);

            return response;
        } catch (InvalidOperationException e) {
            log.error("AI evaluation failed for technology='{}', experience='{}': {}", tech, exp, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during AI evaluation for technology='{}': {}", tech, e.getMessage());
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EVALUATION_COMMUNICATION_FAILED);
        }
    }

    private String buildPrompt(String question, String userAnswer, String technology, String experience, String referenceAnswer) {
        return String.format(
            "You are an expert technical interviewer evaluating a candidate's answer for InterviewBridge.\n\n" +
            "Context:\n" +
            "- Technology: %s\n" +
            "- Experience Level: %s\n" +
            "- Question:\n%s\n\n" +
            "- Reference Answer:\n%s\n\n" +
            "- Candidate's Answer:\n%s\n\n" +
            "Evaluation Instructions:\n" +
            "Evaluate the candidate's actual answer against the question and the reference answer. The evaluation must be fair, rigorous, and calibrated to the candidate's experience level (%s).\n\n" +
            "Criteria:\n" +
            "1. Score (Integer, 0 to 10):\n" +
            "   - Score must accurately reflect the correctness, depth, and clarity of what the candidate actually said.\n" +
            "   - Do NOT always give 8. Differentiate scores across the 0-10 spectrum.\n" +
            "   - A concise but technically correct answer should receive a high score.\n" +
            "   - A verbose/long answer that lacks substance or is wrong should receive a low score.\n" +
            "   - Incorrect technical claims must lower the score.\n" +
            "   - Missing core concepts must lower the score.\n" +
            "   - Completely irrelevant, non-technical, or blank-equivalent answers must receive 0-2.\n" +
            "   - Do not penalize phrasing differences if the underlying technical concept is sound.\n\n" +
            "2. whatWasCorrect:\n" +
            "   - Explicitly detail the accurate technical concepts, definitions, patterns, and explanations present in the candidate's response.\n" +
            "   - If the candidate stated nothing correct, clearly state: \"No technically correct points were identified in the answer.\"\n\n" +
            "3. whatWasMissing:\n" +
            "   - Identify key concepts, trade-offs, edge cases, framework internals, or best practices expected for this level that the candidate missed.\n" +
            "   - If the answer was thoroughly complete and nothing was missed, explicitly state: \"Nothing important was missing; the answer covers all essential points.\"\n\n" +
            "4. improvedAnswer:\n" +
            "   - Provide an interview-ready, polished version of the answer that enhances what the candidate attempted to express.\n" +
            "   - Do NOT simply copy the reference answer.\n\n" +
            "5. explanation:\n" +
            "   - Provide concise, constructive feedback justifying why this specific score was assigned and how the candidate can improve.\n\n" +
            "6. translatedAnswer:\n" +
            "   - Translate or standardize the candidate's answer into clear, professional English. Do not invent facts not mentioned by the candidate.\n\n" +
            "Strict Output Rules:\n" +
            "- Return ONLY valid JSON adhering strictly to the structured schema.\n" +
            "- Do NOT include markdown code blocks (e.g. ```json).\n" +
            "- All 6 fields are required.",
            technology, experience, question,
            referenceAnswer != null && !referenceAnswer.isBlank() ? referenceAnswer : "N/A",
            userAnswer != null && !userAnswer.isBlank() ? userAnswer : "No answer provided.",
            experience
        );
    }

    private GenerateContentConfig buildContentConfig() {
        Schema evaluationSchema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of(
                "translatedAnswer", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("The candidate's answer translated or standardized into clear professional English")
                    .build(),
                "improvedAnswer", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("A refined, interview-ready version improving upon the candidate's answer without copying the reference answer")
                    .build(),
                "explanation", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("Constructive justification for the assigned score")
                    .build(),
                "score", Schema.builder()
                    .type(Type.Known.INTEGER)
                    .description("Integer score between 0 and 10 based on technical correctness")
                    .build(),
                "whatWasCorrect", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("Detailed breakdown of what the candidate got right")
                    .build(),
                "whatWasMissing", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("Detailed breakdown of what was omitted, incorrect, or needed for a complete answer")
                    .build()
            ))
            .required("translatedAnswer", "improvedAnswer", "explanation", "score", "whatWasCorrect", "whatWasMissing")
            .build();

        return GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(evaluationSchema)
            .build();
    }

    private AIEvaluationResponse parseAndValidateResponse(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
        }

        GeminiEvaluationPayload payload;
        try {
            payload = objectMapper.readValue(rawJson, GeminiEvaluationPayload.class);
        } catch (JacksonException e) {
            log.error("Failed to parse Gemini evaluation payload: {}", e.getMessage());
            throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
        }

        if (payload == null) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
        }

        if (payload.score() == null || payload.score() < EntityConstants.PracticeQuestion.MIN_SCORE ||
                payload.score() > EntityConstants.PracticeQuestion.MAX_SCORE) {
            throw new InvalidOperationException(SecurityConstants.MSG_INVALID_SCORE_RANGE);
        }

        if (payload.whatWasCorrect() == null || payload.whatWasCorrect().trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_WHAT_WAS_CORRECT);
        }

        if (payload.whatWasMissing() == null || payload.whatWasMissing().trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_WHAT_WAS_MISSING);
        }

        if (payload.improvedAnswer() == null || payload.improvedAnswer().trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_IMPROVED_ANSWER);
        }

        if (payload.explanation() == null || payload.explanation().trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_EXPLANATION);
        }

        if (payload.translatedAnswer() == null || payload.translatedAnswer().trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_TRANSLATED_ANSWER);
        }

        return new AIEvaluationResponse(
            payload.translatedAnswer().trim(),
            payload.improvedAnswer().trim(),
            payload.explanation().trim(),
            payload.score(),
            payload.whatWasCorrect().trim(),
            payload.whatWasMissing().trim()
        );
    }
}
