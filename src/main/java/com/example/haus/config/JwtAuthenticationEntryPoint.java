package com.example.haus.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = buildErrorJson(HttpServletResponse.SC_UNAUTHORIZED, request.getRequestURI(), "Unauthorized",
                "Invalid or missing JWT Token");
        response.getWriter().write(json);

    }

    private String buildErrorJson(int status, String path, String error, String message) {
        return String.format("""
                {
                    "timestamp": "%s",
                    "status": %d,
                    "path": "%s",
                    "error": "%s",
                    "message": "%s"
                }
                """, LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a", Locale.ENGLISH)),
                status, path, error, message);
    }
}
