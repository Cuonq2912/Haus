package com.example.haus.service.impl;

import com.example.haus.constant.TokenType;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.expiryHour}")
    public long expiryHour;

    @Value("${jwt.expiryDay}")
    public long expiryDay;

    @Value("${jwt.accessKey}")
    public String accessKey;

    @Value("${jwt.refreshKey}")
    public String refreshKey;

    @Override
    public String generateAccessToken(String userId, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generate access token for user{} with authorities{}", username, authorities);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);

        return generateAccessToken(claims, username);
    }

    @Override
    public String generateRefreshToken(String userId, String username, Collection<? extends GrantedAuthority> authorities) {
        log.info("Generate refresh token for user{} with authorities{}", username, authorities);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", authorities);

        return generateAccessToken(claims, username);
    };

    @Override
    public String extractUserName(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    @Override
    public boolean isValid(String token, TokenType type, String name) {
        final String username = extractUserName(token, type);
        return username.equals(name) && !isTokenExpired(token, type);
    }

    @Override
    public boolean isExpired(String token, TokenType type) {
        return isTokenExpired(token, type);
    }

    private String generateAccessToken(Map<String, Object> claims, String username) {
        log.info("------------- [ generateAccessToken]------------------");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryHour))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(Map<String, Object> claims, String username) {
        log.info("------------- [ generateRefreshToken]------------------");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {
        log.info("------------ [ getKey ] -------------------------");
        switch (type) {
            case TokenType.ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
            }
            case TokenType.REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            default -> throw new InvalidDataException("Invalid Token type");
        }
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimsTFunction) {
        log.info("------------- [ extractClaim ] ---------------");
        final Claims claims = extractAllClaims(token, type);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token, TokenType type) {
        log.info("------------- [ extraAllClaims ] --------------------");
        try {
            return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.error("Extra all claim failed, message = {}", e.getMessage());
            throw new AccessDeniedException("Access denied: "+  e.getMessage());
        }
    }

    private boolean isTokenExpired(String token, TokenType type) {
        return extractTokenExpiration(token, type).before(new Date());
    }

    private Date extractTokenExpiration(String token, TokenType type) {
        return extractClaim(token, type, Claims::getExpiration);
    }
}
