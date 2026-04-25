package com.example.haus.filter;

import com.example.haus.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
@Slf4j(topic = "REQUEST-CORRELATION")
public class RequestCorrelationFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = resolveRequestId(request);
        long startMs = System.currentTimeMillis();

        MDC.put("requestId", requestId);
        MDC.put("path", request.getRequestURI());
        MDC.put("method", request.getMethod());
        MDC.put("clientIp", request.getRemoteAddr());
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startMs;

            MDC.put("status", String.valueOf(response.getStatus()));
            MDC.put("durationMs", String.valueOf(durationMs));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails customUserDetails
                    && customUserDetails.getUser() != null && customUserDetails.getUser().getId() != null) {
                MDC.put("userId", String.valueOf(customUserDetails.getUser().getId()));
            }

            log.info("HTTP request completed");
            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String incoming = request.getHeader(REQUEST_ID_HEADER);
        if (StringUtils.hasText(incoming)) {
            return incoming.trim();
        }
        return UUID.randomUUID().toString();
    }
}
