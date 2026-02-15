package com.example.Bhoklagyo.util;

import org.springframework.web.util.HtmlUtils;

/**
 * Utility class for sanitizing user input to prevent XSS and injection attacks.
 * <p>
 * Apply to any user-supplied string before persisting or returning in responses.
 */
public final class InputSanitizer {

    private InputSanitizer() {
        // utility class
    }

    /**
     * HTML-escape a string to neutralise XSS payloads.
     * Returns null for null input.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Trim whitespace and escape HTML special characters
        return HtmlUtils.htmlEscape(input.trim());
    }

    /**
     * Strip all HTML tags from the input.
     * Returns null for null input.
     */
    public static String stripHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<[^>]*>", "").trim();
    }
}
