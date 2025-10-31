package com.automobileproject.EAP.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:myDefaultSecretKeyForDevelopment1234567890123456}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private SecretKey getSigningKey() {
        try {
            // Ensure the secret is exactly 32 characters for HS256
            String safeSecret = secret;
            if (safeSecret.length() < 32) {
                // Pad with zeros to make it 32 characters
                safeSecret = String.format("%-32s", safeSecret).replace(' ', '0');
            } else if (safeSecret.length() > 32) {
                // Truncate to 32 characters
                safeSecret = safeSecret.substring(0, 32);
            }
            logger.info("JWT Secret length: {}", safeSecret.length());
            return Keys.hmacShaKeyFor(safeSecret.getBytes());
        } catch (Exception e) {
            logger.error("Error creating signing key: {}", e.getMessage());
            throw new RuntimeException("Error creating JWT signing key", e);
        }
    }

    public String extractUsername(String token) {
        // This now extracts the email (which is stored as the subject)
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
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
            logger.warn("JWT token expired: {}", e.getMessage());
            throw new RuntimeException("JWT token has expired", e);
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token format: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token format", e);
        } catch (SignatureException e) {
            logger.error("JWT signature validation failed: {}", e.getMessage());
            logger.error("This usually means the JWT secret key has changed or is incorrect");
            throw new RuntimeException("JWT signature validation failed", e);
        } catch (JwtException e) {
            logger.warn("JWT validation error: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // The subject is now the email (userDetails.getUsername() returns email)
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject) // This is now the email
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            logger.info("Generated JWT token for user: {}", subject);
            return token;
        } catch (Exception e) {
            logger.error("Error generating JWT token: {}", e.getMessage());
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractUsername(token); // This now extracts email
            boolean isValid = email.equals(userDetails.getUsername()) && !isTokenExpired(token);
            if (!isValid) {
                logger.warn("JWT token validation failed for user: {}", email);
            }
            return isValid;
        } catch (Exception e) {
            logger.warn("JWT token validation error: {}", e.getMessage());
            return false;
        }
    }
}