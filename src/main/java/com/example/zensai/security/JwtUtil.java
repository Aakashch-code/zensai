package com.example.zensai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:your_super_secret_key_which_must_be_at_least_256_bits_long}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours default
    private long jwtExpiration;

    private SecretKey getSignInKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UUID userId, String role, String workspaceId) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("roles", List.of(role))
                .claim("tenantId", workspaceId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public UUID extractUserId(String token) {
        try {
            return UUID.fromString(extractClaim(token, Claims::getSubject));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid JWT subject");
        }
    }

    public String extractRole(String token) {
        List<String> roles = extractClaim(token, claims -> claims.get("roles", List.class));
        return roles.getFirst();
    }

    public String extractTenantId(String token) {
        return extractClaim(token, claims -> claims.get("tenantId", String.class));
    }

    public boolean isTokenValid(String token, UUID userId) {
        UUID tokenUserId = extractUserId(token);
        return tokenUserId.equals(userId) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload());
    }
}