package com.example.haus.service;

import com.example.haus.constant.TokenType;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface JwtService {
    String generateAccessToken(String userId, String username, Collection<? extends GrantedAuthority> authorities);

    String generateRefreshToken(String userId, String username, Collection<? extends GrantedAuthority> authorities);

    String extractUserName(String token, TokenType type);

    boolean isValid(String token, TokenType type, String username);

    boolean isExpired(String token, TokenType type);
}
