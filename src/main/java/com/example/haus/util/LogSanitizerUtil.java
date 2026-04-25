package com.example.haus.util;

public final class LogSanitizerUtil {

    private LogSanitizerUtil() {
    }

    public static String maskIdentifier(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }

        if (value.contains("@")) {
            String[] parts = value.split("@", 2);
            String local = parts[0];
            String domain = parts.length > 1 ? parts[1] : "";
            String localMasked = local.length() <= 1 ? "*" : local.charAt(0) + "***";
            return localMasked + "@" + domain;
        }

        if (value.length() <= 2) {
            return "**";
        }

        return value.substring(0, 2) + "***";
    }

    public static String sanitizeSensitiveText(String text) {
        if (text == null || text.isBlank()) {
            return "N/A";
        }

        String sanitized = text
                .replaceAll("(?i)(\"?(password|token|refresh_token|access_token|client_secret)\"?\\s*[:=]\\s*\")([^\"]+)(\")",
                        "$1[REDACTED]$4")
                .replaceAll("(?i)\\bBearer\\s+[A-Za-z0-9._-]+", "Bearer [REDACTED]")
                .replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[REDACTED_EMAIL]")
                .replaceAll("eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}", "[REDACTED_JWT]");

        int maxLength = 500;
        if (sanitized.length() > maxLength) {
            return sanitized.substring(0, maxLength) + "...[TRUNCATED]";
        }

        return sanitized;
    }
}
