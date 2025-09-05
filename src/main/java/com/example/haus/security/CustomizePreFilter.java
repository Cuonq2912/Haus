package com.example.haus.security;

import com.example.haus.constant.TokenType;
import com.example.haus.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Slf4j(topic = "CUSTOMIZE-PRE-FILTER")
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomizePreFilter extends OncePerRequestFilter {


    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("{} {}", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

//        log.info("Auth Header = {}", authHeader);

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUserName(token, TokenType.ACCESS_TOKEN);

            if (username.length() > 0 && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtService.isValid(token, TokenType.ACCESS_TOKEN, username)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
        } catch (Exception e) {
            log.error("Invalid token = {}, message = {}", e.getClass(), e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = buildErrorJson(HttpServletResponse.SC_UNAUTHORIZED, request.getRequestURI(), "Unauthorized", "Invalid or missing JWT Token");
            response.getWriter().write(json);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String buildErrorJson(int status, String path, String error, String message) {
        return String.format("""
        {
            "timestamp": "%s",
            "status": %d,
            "path": "%s",
            "error": "%s",
            "message": "%s",
        }
        """,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a", Locale.ENGLISH)),
                status,
                path,
                error,
                message
        );
    }
}
