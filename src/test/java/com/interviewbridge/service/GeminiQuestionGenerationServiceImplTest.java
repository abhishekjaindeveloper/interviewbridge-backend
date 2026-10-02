package com.interviewbridge.service;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.google.genai.types.GenerateContentConfig;
import com.interviewbridge.config.GeminiProperties;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.dto.request.AIQuestionRequest;
import com.interviewbridge.dto.response.AIQuestionResponse;
import com.interviewbridge.exception.InvalidOperationException;
import com.interviewbridge.service.gemini.DefaultGeminiClientWrapper;
import com.interviewbridge.service.gemini.GeminiClientWrapper;
import com.interviewbridge.service.impl.GeminiQuestionGenerationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiQuestionGenerationServiceImplTest {

    @Mock
    private GeminiClientWrapper geminiClientWrapper;

    private GeminiProperties geminiProperties;
    private ObjectMapper objectMapper;
    private GeminiQuestionGenerationServiceImpl service;

    @BeforeEach
    void setUp() {
        geminiProperties = new GeminiProperties();
        geminiProperties.setApiKey("test-dummy-api-key");
        geminiProperties.setModel("gemini-3.5-flash-lite");
        geminiProperties.setTimeoutSeconds(60);

        objectMapper = JsonMapper.builder().build();
        service = new GeminiQuestionGenerationServiceImpl(geminiProperties, geminiClientWrapper, objectMapper);
    }

    @Test
    @DisplayName("A. Unit Test: valid AI response is correctly parsed and converted to AIQuestionResponse list with reference answers")
    void generateQuestions_validResponse_success() {
        String validJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "Explain the difference between HashMap and ConcurrentHashMap in Java.",
                  "referenceAnswer": "HashMap is non-thread-safe and permits null keys/values. ConcurrentHashMap is thread-safe using lock striping or CAS on table buckets without locking the entire map."
                },
                {
                  "questionNumber": 2,
                  "questionText": "How does the Java Garbage Collector identify memory leaks?",
                  "referenceAnswer": "The GC cannot collect objects that are still reachable via the GC roots. Memory leaks occur when unused objects remain strongly reachable through collections, caches, or unclosed resources."
                },
                {
                  "questionNumber": 3,
                  "questionText": "Describe the fork-join framework in Java concurrency.",
                  "referenceAnswer": "The ForkJoinPool uses a work-stealing algorithm to execute divide-and-conquer tasks represented by ForkJoinTask subclasses (RecursiveTask and RecursiveAction)."
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(validJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("Java", "Experienced", 3);
        List<AIQuestionResponse> result = service.generateQuestions(request);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1, result.get(0).questionNumber());
        assertEquals("Explain the difference between HashMap and ConcurrentHashMap in Java.", result.get(0).questionText());
        assertEquals("HashMap is non-thread-safe and permits null keys/values. ConcurrentHashMap is thread-safe using lock striping or CAS on table buckets without locking the entire map.", result.get(0).referenceAnswer());

        assertEquals(2, result.get(1).questionNumber());
        assertEquals("How does the Java Garbage Collector identify memory leaks?", result.get(1).questionText());
        assertEquals("The GC cannot collect objects that are still reachable via the GC roots. Memory leaks occur when unused objects remain strongly reachable through collections, caches, or unclosed resources.", result.get(1).referenceAnswer());

        assertEquals(3, result.get(2).questionNumber());
        assertEquals("Describe the fork-join framework in Java concurrency.", result.get(2).questionText());
        assertEquals("The ForkJoinPool uses a work-stealing algorithm to execute divide-and-conquer tasks represented by ForkJoinTask subclasses (RecursiveTask and RecursiveAction).", result.get(2).referenceAnswer());
    }

    @Test
    @DisplayName("B. Validation Test: wrong question count throws InvalidOperationException")
    void generateQuestions_wrongQuestionCount_throwsException() {
        // Requested 3 questions, but AI returns only 2
        String shortJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "What is dependency injection?",
                  "referenceAnswer": "Dependency injection is a pattern where an object receives its dependencies from an external source."
                },
                {
                  "questionNumber": 2,
                  "questionText": "How does Spring Boot auto-configuration work?",
                  "referenceAnswer": "Auto-configuration attempts to automatically configure beans based on jar dependencies added to classpath."
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(shortJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("Spring Boot", "Senior", 3);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertTrue(ex.getMessage().contains("invalid question count"));
    }

    @Test
    @DisplayName("C. Validation Test: duplicate questions in AI response throws InvalidOperationException")
    void generateQuestions_duplicateQuestions_throwsException() {
        String duplicateJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "What is an index in PostgreSQL?",
                  "referenceAnswer": "An index accelerates search queries on indexed columns."
                },
                {
                  "questionNumber": 2,
                  "questionText": "what is an index in postgresql?",
                  "referenceAnswer": "A B-tree structure providing logarithmic search access."
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(duplicateJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("PostgreSQL", "Mid", 2);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertTrue(ex.getMessage().contains("duplicate questions"));
    }

    @Test
    @DisplayName("D. Validation Test: empty question text throws InvalidOperationException")
    void generateQuestions_emptyQuestionText_throwsException() {
        String emptyQuestionJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "   ",
                  "referenceAnswer": "Some reference answer"
                },
                {
                  "questionNumber": 2,
                  "questionText": "Valid question text",
                  "referenceAnswer": "Another reference answer"
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(emptyQuestionJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("Go", "Junior", 2);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertTrue(ex.getMessage().contains("empty question text"));
    }

    @Test
    @DisplayName("E. Validation Test: missing or blank referenceAnswer throws InvalidOperationException")
    void generateQuestions_blankReferenceAnswer_throwsException() {
        String blankReferenceAnswerJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "Explain goroutines in Go.",
                  "referenceAnswer": "   "
                },
                {
                  "questionNumber": 2,
                  "questionText": "What is a channel in Go?",
                  "referenceAnswer": "Channels are typed conduits through which you can send and receive values."
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(blankReferenceAnswerJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("Go", "Junior", 2);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertEquals(SecurityConstants.MSG_AI_EMPTY_REFERENCE_ANSWER, ex.getMessage());
    }

    @Test
    @DisplayName("F. Validation Test: null referenceAnswer throws InvalidOperationException")
    void generateQuestions_nullReferenceAnswer_throwsException() {
        String nullReferenceAnswerJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "Explain goroutines in Go."
                },
                {
                  "questionNumber": 2,
                  "questionText": "What is a channel in Go?",
                  "referenceAnswer": "Channels are typed conduits through which you can send and receive values."
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(nullReferenceAnswerJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("Go", "Junior", 2);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertEquals(SecurityConstants.MSG_AI_EMPTY_REFERENCE_ANSWER, ex.getMessage());
    }

    @Test
    @DisplayName("Validation Test: non-sequential question numbers throws InvalidOperationException")
    void generateQuestions_nonSequentialNumbers_throwsException() {
        String outOfOrderJsonResponse = """
            {
              "questions": [
                {
                  "questionNumber": 1,
                  "questionText": "Question one",
                  "referenceAnswer": "Answer one"
                },
                {
                  "questionNumber": 3,
                  "questionText": "Question three",
                  "referenceAnswer": "Answer three"
                }
              ]
            }
            """;

        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn(outOfOrderJsonResponse);

        AIQuestionRequest request = new AIQuestionRequest("React", "Junior", 2);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertTrue(ex.getMessage().contains("non-sequential"));
    }

    @Test
    @DisplayName("Error Handling Test: Gemini failure throws clean application-level exception")
    void generateQuestions_geminiApiFailure_throwsApplicationException() {
        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenThrow(new InvalidOperationException(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED));

        AIQuestionRequest request = new AIQuestionRequest("Angular", "Intermediate", 5);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertEquals(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED, ex.getMessage());
    }

    @Test
    @DisplayName("Error Handling Test: malformed JSON from Gemini throws application-level exception")
    void generateQuestions_malformedJson_throwsApplicationException() {
        when(geminiClientWrapper.generateContent(eq("gemini-3.5-flash-lite"), any(), any(GenerateContentConfig.class)))
            .thenReturn("INVALID_NOT_JSON");

        AIQuestionRequest request = new AIQuestionRequest("Docker", "Mid", 3);
        InvalidOperationException ex = assertThrows(InvalidOperationException.class, () -> service.generateQuestions(request));

        assertEquals(SecurityConstants.MSG_AI_MALFORMED_RESPONSE, ex.getMessage());
    }

    @Test
    @DisplayName("Error Handling Test: missing API key in DefaultGeminiClientWrapper throws clear configuration error")
    void clientWrapper_missingApiKey_throwsConfigurationError() {
        GeminiProperties emptyProps = new GeminiProperties();
        emptyProps.setApiKey(null);

        DefaultGeminiClientWrapper wrapper = new DefaultGeminiClientWrapper(emptyProps);
        GenerateContentConfig config = GenerateContentConfig.builder().build();

        InvalidOperationException ex = assertThrows(
            InvalidOperationException.class,
            () -> wrapper.generateContent("gemini-3.5-flash-lite", "test prompt", config)
        );

        assertEquals(SecurityConstants.MSG_AI_API_KEY_MISSING, ex.getMessage());
    }
}
