package com.circuit.usermanagementapi.payload.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtResponseTest {

    private JwtResponse jwtResponse;
    private final String TOKEN = "test-token";
    private final Long ID = 1L;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final List<String> ROLES = Arrays.asList("ROLE_USER", "ROLE_ADMIN");

    @BeforeEach
    void setUp() {
        jwtResponse = new JwtResponse(TOKEN, ID, USERNAME, EMAIL, ROLES);
    }

    @Test
    void testConstructor() {
        assertEquals(TOKEN, jwtResponse.getAccessToken());
        assertEquals(ID, jwtResponse.getId());
        assertEquals(USERNAME, jwtResponse.getUsername());
        assertEquals(EMAIL, jwtResponse.getEmail());
        assertEquals(ROLES, jwtResponse.getRoles());
        assertEquals("Bearer", jwtResponse.getTokenType());
    }

    @Test
    void testSetAccessToken() {
        String newToken = "new-test-token";
        jwtResponse.setAccessToken(newToken);
        assertEquals(newToken, jwtResponse.getAccessToken());
    }

    @Test
    void testSetTokenType() {
        String newType = "NewType";
        jwtResponse.setTokenType(newType);
        assertEquals(newType, jwtResponse.getTokenType());
    }

    @Test
    void testSetId() {
        Long newId = 2L;
        jwtResponse.setId(newId);
        assertEquals(newId, jwtResponse.getId());
    }

    @Test
    void testSetEmail() {
        String newEmail = "newemail@example.com";
        jwtResponse.setEmail(newEmail);
        assertEquals(newEmail, jwtResponse.getEmail());
    }

    @Test
    void testSetUsername() {
        String newUsername = "newusername";
        jwtResponse.setUsername(newUsername);
        assertEquals(newUsername, jwtResponse.getUsername());
    }

    @Test
    void testGetRoles() {
        assertEquals(ROLES, jwtResponse.getRoles());
    }
}