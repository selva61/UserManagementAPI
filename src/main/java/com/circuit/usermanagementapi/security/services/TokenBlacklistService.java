package com.circuit.usermanagementapi.security.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing blacklisted JWT tokens.
 * This service keeps track of tokens that have been invalidated through logout.
 */
@Service
public class TokenBlacklistService {
    
    // Using ConcurrentHashMap for thread safety
    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    
    /**
     * Adds a token to the blacklist
     * 
     * @param token The JWT token to blacklist
     * @param expirationTime The expiration time of the token in milliseconds
     */
    public void blacklistToken(String token, long expirationTime) {
        blacklistedTokens.put(token, expirationTime);
    }
    
    /**
     * Checks if a token is blacklisted
     * 
     * @param token The JWT token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }
    
    /**
     * Removes expired tokens from the blacklist
     * This method should be called periodically to clean up the blacklist
     */
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        Set<String> tokensToRemove = new HashSet<>();
        
        blacklistedTokens.forEach((token, expirationTime) -> {
            if (expirationTime < currentTime) {
                tokensToRemove.add(token);
            }
        });
        
        tokensToRemove.forEach(blacklistedTokens::remove);
    }
}