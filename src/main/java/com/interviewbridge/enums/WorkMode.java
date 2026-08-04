package com.interviewbridge.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enumeration representing preferred work modes for career preferences.
 */
public enum WorkMode {
    REMOTE,
    HYBRID,
    ONSITE;

    @JsonCreator
    public static WorkMode fromString(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        for (WorkMode mode : WorkMode.values()) {
            if (mode.name().equalsIgnoreCase(trimmed)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown work mode: " + value);
    }
}
