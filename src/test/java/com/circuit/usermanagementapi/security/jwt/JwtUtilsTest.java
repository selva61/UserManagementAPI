package com.circuit.usermanagementapi.security.jwt;

import com.circuit.usermanagementapi.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;
    private final String USERNAME = "testuser";
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up UserDetailsImpl
        userDetails = new UserDetailsImpl(
                USER_ID,
                USERNAME,
                "test@example.com",
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
        );
        
        // Mock authentication
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        // Set JWT properties using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyWhichNeedsToBeLongEnoughForTheAlgorithm");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60000); // 1 minute
    }

    @Test
    void testGenerateJwtToken() {
        // Generate token
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Assert that the token is not null or empty
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUserNameFromJwtToken() {
        // Generate token
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Get username from token
        String username = jwtUtils.getUserNameFromJwtToken(token);
        
        // Assert that the username matches
        assertEquals(USERNAME, username);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        // Generate token
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Validate token
        boolean isValid = jwtUtils.validateJwtToken(token);
        
        // Assert that the token is valid
        assertTrue(isValid);
    }

    @Test
    void testValidateJwtToken_InvalidToken() {
        // Test with invalid token
        boolean isValid = jwtUtils.validateJwtToken("invalidToken");
        
        // Assert that the token is invalid
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        // Test with empty token
        boolean isValid = jwtUtils.validateJwtToken("");
        
        // Assert that the token is invalid
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_ExpiredToken() throws Exception {
        // Set a very short expiration time
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1); // 1 millisecond
        
        // Generate token
        String token = jwtUtils.generateJwtToken(authentication);
        
        // Wait for the token to expire
        Thread.sleep(10);
        
        // Validate token
        boolean isValid = jwtUtils.validateJwtToken(token);
        
        // Assert that the token is invalid
        assertFalse(isValid);
    }
}