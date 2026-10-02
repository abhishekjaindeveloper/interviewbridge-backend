package com.interviewbridge.service;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.google.genai.types.GenerateContentConfig;
import com.interviewbridge.config.GeminiProperties;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.AIEvaluationRequest;
import com.interviewbridge.dto.response.AIEvaluationResponse;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.service.gemini.GeminiClientWrapper;
import com.interviewbridge.service.impl.AIEvaluationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIEvaluationServiceImplTest {

    @Mock
    private GeminiClientWrapper geminiClientWrapper;

    private GeminiProperties geminiProperties;
    private ObjectMapper objectMapper;
    private AIEvaluationServiceImpl service;

    @BeforeEach
    void setUp() {
        geminiProperties = new GeminiProperties();
        geminiProperties.setApiKey("test-dummy-api-key");
        geminiProperties.setModel("gemini-3.5-flash-lite");
        geminiProperties.setTimeoutSeconds(60);

        objectMapper = JsonMapper.builder().build();
        service = new AIEvaluationServiceImpl(geminiProperties, geminiClientWrapper, objectMapper);
    }

    @Test
    @DisplayName("A. Valid Gemini evaluation response is parsed correctly with real scores")
    void evaluateAnswer_validResponse_success() {
        String validJsonResponse = """
            {
              "translatedAnswer": "Polymorphism allows objects to take multiple forms in object-oriented programming.",
              "improvedAnswer": "Polymorphism enables objects to be treated as instances of their parent class or interface, implemented via dynamic dispatch.",
              "explanation": "Clear definition of core polymorphism mechanics with accurate conceptual summary.",
              "score": 9,
              "whatWasCorrect": "Accurately explained interface implementation and dynamic method invocation.",
              "whatWasMissing": "Could provide a brief mention of compile-time overloading versus runtime overriding."
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(validJsonResponse);

        AIEvaluationRequest request = new AIEvaluationRequest(
            "What is polymorphism in Java?",
            "Polymorphism allows objects to take multiple forms.",
            "Java",
            "Mid-Level",
            "Polymorphism is an OOP principle allowing one interface to be used for a general class of actions."
        );

        AIEvaluationResponse result = service.evaluateAnswer(request);

        assertNotNull(result);
        assertEquals(9, result.score());
        assertEquals("Polymorphism allows objects to take multiple forms in object-oriented programming.", result.translatedAnswer());
        assertEquals("Polymorphism enables objects to be treated as instances of their parent class or interface, implemented via dynamic dispatch.", result.improvedAnswer());
        assertEquals("Clear definition of core polymorphism mechanics with accurate conceptual summary.", result.explanation());
        assertEquals("Accurately explained interface implementation and dynamic method invocation.", result.whatWasCorrect());
        assertEquals("Could provide a brief mention of compile-time overloading versus runtime overriding.", result.whatWasMissing());
    }

    @Test
    @DisplayName("B. Score 0 is accepted as a valid score")
    void evaluateAnswer_scoreZero_accepted() {
        String jsonWithScoreZero = """
            {
              "translatedAnswer": "I do not know the answer.",
              "improvedAnswer": "Dependency injection is a design pattern where an object receives its dependencies from external assembler code.",
              "explanation": "No technical explanation was provided by the candidate.",
              "score": 0,
              "whatWasCorrect": "No technically correct points were identified in the answer.",
              "whatWasMissing": "Complete explanation of IoC, constructor injection, and bean lifecycle."
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonWithScoreZero);

        AIEvaluationRequest request = new AIEvaluationRequest("What is DI?", "I do not know", "Spring", "Junior", "DI explanation");
        AIEvaluationResponse result = service.evaluateAnswer(request);

        assertNotNull(result);
        assertEquals(0, result.score());
    }

    @Test
    @DisplayName("C. Score 10 is accepted as a valid score")
    void evaluateAnswer_scoreTen_accepted() {
        String jsonWithScoreTen = """
            {
              "translatedAnswer": "Perfect explanation of ACID properties in transactional databases.",
              "improvedAnswer": "Flawless and concise interview response.",
              "explanation": "Mastery of transactional consistency and concurrency isolation demonstrated.",
              "score": 10,
              "whatWasCorrect": "Full coverage of Atomicity, Consistency, Isolation, and Durability with write-ahead logging.",
              "whatWasMissing": "Nothing important was missing; the answer covers all essential points."
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonWithScoreTen);

        AIEvaluationRequest request = new AIEvaluationRequest("Explain ACID", "Candidate answer", "PostgreSQL", "Senior", "ACID reference");
        AIEvaluationResponse result = service.evaluateAnswer(request);

        assertNotNull(result);
        assertEquals(10, result.score());
    }

    @Test
    @DisplayName("D. Score below 0 is rejected")
    void evaluateAnswer_scoreBelowZero_rejected() {
        String jsonNegativeScore = """
            {
              "translatedAnswer": "Some answer",
              "improvedAnswer": "Some improvement",
              "explanation": "Some explanation",
              "score": -1,
              "whatWasCorrect": "Some points",
              "whatWasMissing": "Some gaps"
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonNegativeScore);

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_INVALID_SCORE_RANGE, ex.getMessage());
    }

    @Test
    @DisplayName("E. Score above 10 is rejected")
    void evaluateAnswer_scoreAboveTen_rejected() {
        String jsonHighScore = """
            {
              "translatedAnswer": "Some answer",
              "improvedAnswer": "Some improvement",
              "explanation": "Some explanation",
              "score": 11,
              "whatWasCorrect": "Some points",
              "whatWasMissing": "Some gaps"
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonHighScore);

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_INVALID_SCORE_RANGE, ex.getMessage());
    }

    @Test
    @DisplayName("F. Missing whatWasCorrect is rejected")
    void evaluateAnswer_missingWhatWasCorrect_rejected() {
        String jsonMissingWhatWasCorrect = """
            {
              "translatedAnswer": "Some answer",
              "improvedAnswer": "Some improvement",
              "explanation": "Some explanation",
              "score": 7,
              "whatWasCorrect": "   ",
              "whatWasMissing": "Some gaps"
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonMissingWhatWasCorrect);

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_AI_EMPTY_WHAT_WAS_CORRECT, ex.getMessage());
    }

    @Test
    @DisplayName("G. Missing whatWasMissing is rejected")
    void evaluateAnswer_missingWhatWasMissing_rejected() {
        String jsonMissingWhatWasMissing = """
            {
              "translatedAnswer": "Some answer",
              "improvedAnswer": "Some improvement",
              "explanation": "Some explanation",
              "score": 7,
              "whatWasCorrect": "Good points",
              "whatWasMissing": ""
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonMissingWhatWasMissing);

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_AI_EMPTY_WHAT_WAS_MISSING, ex.getMessage());
    }

    @Test
    @DisplayName("H. Missing improvedAnswer is rejected")
    void evaluateAnswer_missingImprovedAnswer_rejected() {
        String jsonMissingImproved = """
            {
              "translatedAnswer": "Some answer",
              "improvedAnswer": " ",
              "explanation": "Some explanation",
              "score": 7,
              "whatWasCorrect": "Good points",
              "whatWasMissing": "Gaps"
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonMissingImproved);

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_AI_EMPTY_IMPROVED_ANSWER, ex.getMessage());
    }

    @Test
    @DisplayName("I. Missing explanation is rejected")
    void evaluateAnswer_missingExplanation_rejected() {
        String jsonMissingExplanation = """
            {
              "translatedAnswer": "Some answer",
              "improvedAnswer": "Improved answer",
              "explanation": "",
              "score": 7,
              "whatWasCorrect": "Good points",
              "whatWasMissing": "Gaps"
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(jsonMissingExplanation);

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_AI_EMPTY_EXPLANATION, ex.getMessage());
    }

    @Test
    @DisplayName("J. Gemini failure is handled cleanly as application exception")
    void evaluateAnswer_geminiFailure_throwsApplicationException() {
        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenThrow(new RuntimeException("Gemini HTTP timeout"));

        AIEvaluationRequest request = new AIEvaluationRequest("Q", "A", "Java", "Mid", "Ref");
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.evaluateAnswer(request));

        assertEquals(SecurityConstants.MSG_AI_EVALUATION_COMMUNICATION_FAILED, ex.getMessage());
    }
}
