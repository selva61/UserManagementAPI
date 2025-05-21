package com.circuit.usermanagementapi.security.jwt;

import com.circuit.usermanagementapi.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        if (token == null) {
            logger.error("Cannot extract username from null token");
            return null;
        }

        logger.debug("Extracting username from JWT token: length={}", token.length());

        try {
            logger.debug("Trying to extract username with key (Base64-encoded)");
            String username = Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody().getSubject();
            logger.debug("Username extracted successfully: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extracts the expiration time from a JWT token
     * 
     * @param token The JWT token
     * @return The expiration time in milliseconds
     */
    public long getExpirationFromJwtToken(String token) {
        if (token == null) {
            logger.error("Cannot extract expiration from null token");
            throw new IllegalArgumentException("Token cannot be null");
        }

        logger.debug("Extracting expiration from JWT token: length={}", token.length());

        try {
            Date expiration = Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody().getExpiration();
            logger.debug("Expiration extracted successfully: {}", expiration);
            return expiration.getTime();
        } catch (Exception e) {
            logger.error("Failed to extract expiration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract expiration from token", e);
        }
    }

    public boolean validateJwtToken(String authToken) {
        if (authToken == null) {
            logger.error("JWT token is null");
            return false;
        }

        logger.debug("Validating JWT token: length={}", authToken.length());

        try {
            logger.debug("Validating JWT token with secret key (Base64-encoded)");
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            logger.debug("JWT token validated successfully");

            // Log some token details for debugging
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken).getBody();
                logger.debug("JWT token details - Subject: {}, Issued: {}, Expiration: {}", 
                           claims.getSubject(), claims.getIssuedAt(), claims.getExpiration());
            } catch (Exception e) {
                logger.debug("Could not extract token details: {}", e.getMessage());
            }

            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage(), e);
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error validating JWT token: {}", e.getMessage(), e);
        }

        return false;
    }
}
