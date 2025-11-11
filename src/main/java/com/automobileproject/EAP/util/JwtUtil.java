package com.automobileproject.EAP.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:myDefaultSecretKeyForDevelopment1234567890123456}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private SecretKey getSigningKey() {
        String normalizedSecret = normalizeSecret(secret);
        log.debug("JWT Secret length: {}", normalizedSecret.length());
        return Keys.hmacShaKeyFor(normalizedSecret.getBytes());
    }

    private String normalizeSecret(String secret) {
        if (secret.length() < 32) {
            return String.format("%-32s", secret).replace(' ', '0');
        } else if (secret.length() > 32) {
            return secret.substring(0, 32);
        }
        return secret;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired");
            throw new RuntimeException("JWT token has expired", e);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token format");
            throw new RuntimeException("Invalid JWT token format", e);
        } catch (SignatureException e) {
            log.error("JWT signature validation failed");
            throw new RuntimeException("JWT signature validation failed", e);
        } catch (JwtException e) {
            log.warn("JWT validation error: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.warn("Error checking token expiration");
            return true;
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        log.info("Generating token for user: {}", userDetails.getUsername());
        log.info("User authorities: {}", userDetails.getAuthorities());
        
        // Extract role from authorities
        userDetails.getAuthorities().stream()
                .findFirst()
                .ifPresent(authority -> {
                    String role = authority.getAuthority();
                    log.info("Found authority: {}", role);
                    // Remove "ROLE_" prefix if present
                    if (role.startsWith("ROLE_")) {
                        role = role.substring(5);
                    }
                    claims.put("role", role);
                    log.info("Added role to JWT claims: {}", role);
                });
        
        log.info("Final claims map: {}", claims);
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Generated JWT token for user: {}", subject);
        return token;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            String email = extractUsername(token);
            boolean isValid = email.equals(userDetails.getUsername()) && !isTokenExpired(token);

            if (!isValid) {
                log.warn("JWT token validation failed for user: {}", email);
            }

            return isValid;
        } catch (Exception e) {
            log.warn("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }
}