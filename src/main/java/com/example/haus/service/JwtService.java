package com.example.haus.service;

import com.example.haus.domain.entity.user.User;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateToken(User user, long expirationTime) throws JOSEException;

    String extractUsername(String token);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenNotExpired(String token);

}
