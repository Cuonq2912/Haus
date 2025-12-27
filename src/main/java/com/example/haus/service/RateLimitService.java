package com.example.haus.service;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimitService {
    boolean allowRequest(HttpServletRequest request);
}
