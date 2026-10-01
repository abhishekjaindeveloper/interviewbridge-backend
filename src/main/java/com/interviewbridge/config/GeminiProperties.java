package com.interviewbridge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Gemini AI integration.
 */
@Configuration
@ConfigurationProperties(prefix = "gemini")
@Getter
@Setter
public class GeminiProperties {

    /**
     * API key for Google Gemini API, typically injected via GEMINI_API_KEY environment variable.
     */
    private String apiKey;

    /**
     * The Gemini model to use for generation (defaults to gemini-3.5-flash-lite).
     */
    private String model = "gemini-3.5-flash-lite";

    /**
     * Client timeout in seconds (defaults to 60s).
     */
    private int timeoutSeconds = 60;

    /**
     * Checks whether an API key has been configured and is non-blank.
     *
     * @return true if valid API key is present
     */
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
