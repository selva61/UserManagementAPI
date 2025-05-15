package com.circuit.usermanagementapi.security;

import com.circuit.usermanagementapi.security.services.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSecurityTest {

    private UserSecurity userSecurity;
    private final Long USER_ID = 1L;
    private final Long DIFFERENT_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        userSecurity = new UserSecurity();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testIsCurrentUser_WhenUserIdMatches() {
        // Create UserDetailsImpl with USER_ID
        UserDetailsImpl userDetails = new UserDetailsImpl(
                USER_ID,
                "testuser",
                "test@example.com",
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
        );
        
        // Set authentication in security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Call the method
        boolean result = userSecurity.isCurrentUser(USER_ID);
        
        // Assert that the result is true
        assertTrue(result);
    }

    @Test
    void testIsCurrentUser_WhenUserIdDoesNotMatch() {
        // Create UserDetailsImpl with USER_ID
        UserDetailsImpl userDetails = new UserDetailsImpl(
                USER_ID,
                "testuser",
                "test@example.com",
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
        );
        
        // Set authentication in security context
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Call the method with a different user ID
        boolean result = userSecurity.isCurrentUser(DIFFERENT_USER_ID);
        
        // Assert that the result is false
        assertFalse(result);
    }

    @Test
    void testIsCurrentUser_WhenNotAuthenticated() {
        // Do not set authentication in security context
        
        // Call the method
        boolean result = userSecurity.isCurrentUser(USER_ID);
        
        // Assert that the result is false
        assertFalse(result);
    }

    @Test
    void testIsCurrentUser_WhenPrincipalIsNotUserDetailsImpl() {
        // Set authentication with a different principal type
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "username", "password", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Call the method
        boolean result = userSecurity.isCurrentUser(USER_ID);
        
        // Assert that the result is false
        assertFalse(result);
    }

    @Test
    void testIsCurrentUser_WhenAuthenticationIsNull() {
        // Set null authentication in security context
        SecurityContextHolder.getContext().setAuthentication(null);
        
        // Call the method
        boolean result = userSecurity.isCurrentUser(USER_ID);
        
        // Assert that the result is false
        assertFalse(result);
    }
}