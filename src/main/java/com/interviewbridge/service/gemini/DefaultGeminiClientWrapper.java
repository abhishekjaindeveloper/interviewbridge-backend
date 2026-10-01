package com.interviewbridge.service.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import com.interviewbridge.config.GeminiProperties;
import com.interviewbridge.constants.SecurityConstants;
import com.interviewbridge.exception.InvalidOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Production implementation of GeminiClientWrapper utilizing the official Google Gen AI Java SDK.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultGeminiClientWrapper implements GeminiClientWrapper {

    private final GeminiProperties geminiProperties;
    private volatile Client client;

    @Override
    public String generateContent(String model, String prompt, GenerateContentConfig config) {
        if (!geminiProperties.hasApiKey()) {
            throw new InvalidOperationException(SecurityConstants.MSG_AI_API_KEY_MISSING);
        }

        try {
            Client activeClient = getOrCreateClient();
            GenerateContentResponse response = activeClient.models.generateContent(model, prompt, config);
            if (response == null) {
                throw new InvalidOperationException(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED);
            }
            return response.text();
        } catch (InvalidOperationException e) {
            throw e;
        } catch (Exception e) {
            logGeminiFailureDiagnostics(e);
            throw new InvalidOperationException(SecurityConstants.MSG_AI_SERVICE_COMMUNICATION_FAILED);
        }
    }

    private void logGeminiFailureDiagnostics(Exception e) {
        String exClassName = e.getClass().getName();
        String exMessage = sanitize(e.getMessage());

        Throwable rootCause = getRootCause(e);
        String rootCauseClassName = rootCause != null ? rootCause.getClass().getName() : "N/A";
        String rootCauseMessage = rootCause != null ? sanitize(rootCause.getMessage()) : "N/A";
        String causeChain = buildCauseChain(e);

        log.error("==================== GEMINI API FAILURE DIAGNOSTICS ====================");
        log.error("Exception class: {}", exClassName);
        log.error("Exception message: {}", exMessage);
        log.error("Cause chain: {}", causeChain);
        log.error("Root cause class: {}", rootCauseClassName);
        log.error("Root cause message: {}", rootCauseMessage);
        log.error("Full stack trace:", e);
        log.error("========================================================================");
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause != null && cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    private String buildCauseChain(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        Throwable current = throwable;
        int depth = 0;
        while (current != null && depth < 20) {
            if (depth > 0) {
                sb.append(" -> ");
            }
            sb.append(current.getClass().getSimpleName())
              .append("(\"")
              .append(sanitize(current.getMessage()))
              .append("\")");
            Throwable next = current.getCause();
            if (next == current) {
                break;
            }
            current = next;
            depth++;
        }
        return sb.toString();
    }

    private String sanitize(String input) {
        if (input == null) {
            return "null";
        }
        String sanitized = input;
        String apiKey = geminiProperties.getApiKey();
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            sanitized = sanitized.replace(apiKey, "[REDACTED_API_KEY]");
        }
        sanitized = sanitized.replaceAll("(?i)(key|token|bearer|authorization)[=:\\s]+([a-zA-Z0-9_\\-]{15,})", "$1=[REDACTED]");
        return sanitized;
    }

    private Client getOrCreateClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    // Google GenAI SDK HttpOptions.timeout takes milliseconds (converted to Duration.ofMillis)
                    int timeoutSeconds = geminiProperties.getTimeoutSeconds() > 0 ? geminiProperties.getTimeoutSeconds() : 60;
                    int timeoutMs = timeoutSeconds * 1000;

                    client = Client.builder()
                        .apiKey(geminiProperties.getApiKey())
                        .httpOptions(HttpOptions.builder()
                            .timeout(timeoutMs)
                            .build())
                        .build();
                }
            }
        }
        return client;
    }
}
