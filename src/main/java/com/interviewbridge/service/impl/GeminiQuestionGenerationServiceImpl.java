package com.interviewbridge.service.impl;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.interviewbridge.config.GeminiProperties;
import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.AIQuestionRequest;
import com.interviewbridge.dto.response.AIQuestionResponse;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.service.AIQuestionGenerationService;
import com.interviewbridge.service.gemini.GeminiClientWrapper;
import com.interviewbridge.service.gemini.GeminiQuestionsPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Production implementation of AIQuestionGenerationService powered by Google's Gemini API.
 * Employs structured JSON schema enforcement, strict validation, and resilient error handling.
 */
@Service
@Profile("!mock")
@RequiredArgsConstructor
@Slf4j
public class GeminiQuestionGenerationServiceImpl implements AIQuestionGenerationService {

    private final GeminiProperties geminiProperties;
    private final GeminiClientWrapper geminiClientWrapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<AIQuestionResponse> generateQuestions(AIQuestionRequest request) {
        String tech = request.technologyName() != null ? request.technologyName().trim() : "Software Engineering";
        String exp = request.experienceLabel() != null ? request.experienceLabel().trim() : "Intermediate";
        int requestedCount = request.numberOfQuestions() > 0
                ? request.numberOfQuestions()
                : EntityConstants.PracticeSession.DEFAULT_TOTAL_QUESTIONS;

        log.info("Initiating Gemini question generation: technology='{}', experience='{}', count={}, model='{}'",
                tech, exp, requestedCount, geminiProperties.getModel());

        long startTime = System.currentTimeMillis();

        try {
            String prompt = buildPrompt(tech, exp, requestedCount);
            GenerateContentConfig config = buildContentConfig();

            String rawJson = geminiClientWrapper.generateContent(
                    geminiProperties.getModel(),
                    prompt,
                    config
            );

            List<AIQuestionResponse> responses = parseAndValidateResponse(rawJson, requestedCount);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully generated {} interview questions for technology='{}' in {}ms",
                    responses.size(), tech, duration);

            return responses;
        } catch (InvalidOperationException e) {
            log.error("AI question generation failed for technology='{}', experience='{}': {}", tech, exp, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during AI question generation for technology='{}': {}", tech, e.getMessage());
            throw new InvalidOperationException(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED);
        }
    }

    private String buildPrompt(String technology, String experienceLevel, int questionCount) {
        return String.format(
            "You are an expert technical interviewer conducting an interview for InterviewBridge.\n" +
            "Generate exactly %d realistic interview questions along with high-quality reference answers for a candidate specializing in \"%s\" at the \"%s\" experience level.\n\n" +
            "Guidelines:\n" +
            "1. Relevance & Depth: Questions must be technically precise and specifically tailored to \"%s\". Do not produce generic filler.\n" +
            "2. Experience Calibration: Both the questions and reference answers must accurately match the \"%s\" seniority level.\n" +
            "   - Junior: Focus on fundamentals, core syntax, OOP, standard libraries, and basic problem solving.\n" +
            "   - Mid-Level: Focus on framework internals, architecture, practical design decisions, error handling, and performance.\n" +
            "   - Senior/Lead: Focus on deep runtime internals, concurrency, distributed patterns, scalability, production debugging, and system trade-offs.\n" +
            "3. Multi-Area Coverage: Cover a diverse range of technical areas (e.g., core language, data structures, frameworks, concurrency, architecture, testing, and troubleshooting).\n" +
            "4. Question Mixture: Balance conceptual understanding, practical application, and real-world scenario/problem-solving questions.\n" +
            "5. Uniqueness: Each question must be completely distinct. Do not duplicate topics or phrasing within the set.\n" +
            "6. Reference Answer Quality:\n" +
            "   - Provide a clear, technically accurate, interview-oriented reference answer for each question.\n" +
            "   - Explain the essential concepts, mechanisms, and best practices expected from a strong candidate.\n" +
            "   - Ensure the reference answer is educational and useful for a candidate who does not know the answer.\n" +
            "   - Keep it concise and focused without being excessively long.\n" +
            "7. Strict Output Rules:\n" +
            "   - Return ONLY the structured JSON response.\n" +
            "   - Do NOT include markdown code block wrappers (e.g. ```json ... ```).\n" +
            "   - Do NOT include question number prefixes in questionText (e.g., write 'What is...' instead of '1. What is...').\n" +
            "   - Return exactly %d questions numbered sequentially from 1 to %d in the structured format with questionNumber, questionText, and referenceAnswer.",
            questionCount, technology, experienceLevel,
            technology,
            experienceLevel,
            questionCount, questionCount
        );
    }

    private GenerateContentConfig buildContentConfig() {
        Schema questionItemSchema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of(
                "questionNumber", Schema.builder()
                    .type(Type.Known.INTEGER)
                    .description("Sequential 1-based question number")
                    .build(),
                "questionText", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("The interview question text without any number prefix")
                    .build(),
                "referenceAnswer", Schema.builder()
                    .type(Type.Known.STRING)
                    .description("A high-quality, technically accurate reference answer explaining key concepts expected from the candidate")
                    .build()
            ))
            .required("questionNumber", "questionText", "referenceAnswer")
            .build();

        Schema questionsSchema = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(Map.of(
                "questions", Schema.builder()
                    .type(Type.Known.ARRAY)
                    .items(questionItemSchema)
                    .description("List of generated interview questions and reference answers")
                    .build()
            ))
            .required("questions")
            .build();

        return GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(questionsSchema)
            .build();
    }

    private List<AIQuestionResponse> parseAndValidateResponse(String rawJson, int expectedCount) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
        }

        GeminiQuestionsPayload payload;
        try {
            payload = objectMapper.readValue(rawJson, GeminiQuestionsPayload.class);
        } catch (JacksonException e) {
            log.error("Failed to parse Gemini JSON payload: {}", e.getMessage());
            throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
        }

        if (payload == null || payload.questions() == null) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
        }

        List<GeminiQuestionsPayload.GeminiQuestionItem> items = payload.questions();
        if (items.size() != expectedCount) {
            throw new InvalidOperationException(
                String.format(SecurityConstants.MSG_AI_INVALID_QUESTION_COUNT, expectedCount, items.size())
            );
        }

        List<AIQuestionResponse> responses = new ArrayList<>(expectedCount);
        Set<String> seenNormalizedTexts = new HashSet<>();

        for (int i = 0; i < items.size(); i++) {
            GeminiQuestionsPayload.GeminiQuestionItem item = items.get(i);
            int expectedNumber = i + 1;

            if (item == null) {
                throw new InvalidOperationException(SecurityConstants.MSG_AI_MALFORMED_RESPONSE);
            }

            if (item.questionNumber() == null || item.questionNumber() != expectedNumber) {
                throw new InvalidOperationException(SecurityConstants.MSG_AI_NON_SEQUENTIAL_NUMBERS);
            }

            String questionText = item.questionText();
            if (questionText == null || questionText.trim().isEmpty()) {
                throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_QUESTION_TEXT);
            }

            String cleanText = questionText.trim();
            String normalizedText = cleanText.toLowerCase();
            if (!seenNormalizedTexts.add(normalizedText)) {
                throw new InvalidOperationException(SecurityConstants.MSG_AI_DUPLICATE_QUESTIONS);
            }

            String refAnswer = item.referenceAnswer();
            if (refAnswer == null || refAnswer.trim().isEmpty()) {
                throw new InvalidOperationException(SecurityConstants.MSG_AI_EMPTY_REFERENCE_ANSWER);
            }
            String cleanRefAnswer = refAnswer.trim();

            responses.add(new AIQuestionResponse(expectedNumber, cleanText, cleanRefAnswer));
        }

        return responses;
    }
}
