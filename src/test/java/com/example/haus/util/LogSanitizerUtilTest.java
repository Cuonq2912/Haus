package com.example.haus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogSanitizerUtilTest {

    @Test
    void shouldMaskEmailIdentifier() {
        String masked = LogSanitizerUtil.maskIdentifier("john.doe@example.com");
        assertEquals("j***@example.com", masked);
    }

    @Test
    void shouldRedactSensitivePairs() {
        String input = "{\"password\":\"secret\",\"token\":\"abc123\"}";
        String sanitized = LogSanitizerUtil.sanitizeSensitiveText(input);

        assertTrue(sanitized.contains("\"password\":\"[REDACTED]\""));
        assertTrue(sanitized.contains("\"token\":\"[REDACTED]\""));
    }

    @Test
    void shouldRedactBearerAndEmail() {
        String input = "Authorization: Bearer abcd.efgh.ijkl, user=test.user@example.com";
        String sanitized = LogSanitizerUtil.sanitizeSensitiveText(input);

        assertTrue(sanitized.contains("Bearer [REDACTED]"));
        assertTrue(sanitized.contains("[REDACTED_EMAIL]"));
    }

    @Test
    void shouldTruncateLongPayload() {
        String input = "x".repeat(600);
        String sanitized = LogSanitizerUtil.sanitizeSensitiveText(input);

        assertTrue(sanitized.endsWith("...[TRUNCATED]"));
        assertTrue(sanitized.length() <= 514);
    }
}
