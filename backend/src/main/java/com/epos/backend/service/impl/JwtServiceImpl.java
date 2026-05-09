package com.epos.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.epos.backend.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    /* -------------------------------- FUNCTION PRIVATE -------------------------------------- */
    private Boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        SecretKey signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    @Override
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryAt = new Date(now.getTime() + expiration);
        SecretKey signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryAt)
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public Boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

}
