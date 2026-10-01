package com.interviewbridge.service.gemini;

import com.google.genai.types.GenerateContentConfig;

/**
 * Abstraction over the Gemini SDK Client to facilitate resilience, testing, and provider isolation.
 */
public interface GeminiClientWrapper {

    /**
     * Generates text content using the specified model, prompt, and configuration.
     *
     * @param model the model identifier
     * @param prompt the user prompt
     * @param config the generation configuration (e.g. JSON schema, response MIME type)
     * @return the raw generated text response
     */
    String generateContent(String model, String prompt, GenerateContentConfig config);
}
