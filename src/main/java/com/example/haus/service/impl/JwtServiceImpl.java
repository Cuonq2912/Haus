package com.example.haus.service.impl;

import com.example.haus.domain.entity.user.User;
import com.example.haus.repository.InvalidatedTokenRepository;
import com.example.haus.service.JwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService {

    @NonFinal
    @Value("${jwt.secret}")
    String secretKey;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public String generateToken(User user, long expirationTime) throws JOSEException {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + expirationTime))
                .jwtID(UUID.randomUUID().toString())
                .claim("authorities", List.of("ROLE_" + user.getRole().name()))
                .claim("userID", user.getId())
                .claim("email", user.getEmail())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS512),
                jwtClaimsSet
        );

        signedJWT.sign(new MACSigner(secretKey.getBytes()));

        return signedJWT.serialize();

    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenNotExpired(token);
    }

    @Override
    public boolean isTokenNotExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }


    @Scheduled(cron = "0 0 3 * * *")
    public void cleanExpiredInvalidatedTokens() {
        Date now = new Date();
        invalidatedTokenRepository.deleteByExpiryTimeBefore(now);
    }
}
