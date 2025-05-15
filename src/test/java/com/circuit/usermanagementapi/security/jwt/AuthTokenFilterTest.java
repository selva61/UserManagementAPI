package com.circuit.usermanagementapi.security.jwt;

import com.circuit.usermanagementapi.security.services.UserDetailsImpl;
import com.circuit.usermanagementapi.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private UserDetailsImpl userDetails;
    private final String USERNAME = "testuser";
    private final String TOKEN = "validToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        // Set up UserDetailsImpl
        userDetails = new UserDetailsImpl(
                1L,
                USERNAME,
                "test@example.com",
                "password123",
                List.of(new SimpleGrantedAuthority("ROLE_TEAM_MEMBER"))
        );
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        // Mock request to return a valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);

        // Mock JWT utils to validate the token and return a username
        when(jwtUtils.validateJwtToken(TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(TOKEN)).thenReturn(USERNAME);

        // Mock user details service to return user details
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain was called
        verify(filterChain).doFilter(request, response);

        // Verify that the authentication was set in the security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        // Mock request to return an invalid token
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        // Mock JWT utils to invalidate the token
        when(jwtUtils.validateJwtToken("invalidToken")).thenReturn(false);

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain was called
        verify(filterChain).doFilter(request, response);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify that user details service was not called
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testDoFilterInternal_WithNoToken() throws ServletException, IOException {
        // Mock request to return no token
        when(request.getHeader("Authorization")).thenReturn(null);

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain was called
        verify(filterChain).doFilter(request, response);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify that JWT utils and user details service were not called
        verify(jwtUtils, never()).validateJwtToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testDoFilterInternal_WithException() throws ServletException, IOException {
        // Mock request to return a valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);

        // Mock JWT utils to validate the token but throw an exception when getting username
        when(jwtUtils.validateJwtToken(TOKEN)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(TOKEN)).thenThrow(new RuntimeException("Test exception"));

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that the filter chain was called despite the exception
        verify(filterChain).doFilter(request, response);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testParseJwt_WithValidHeader() throws Exception {
        // Mock request to return a valid Authorization header
        when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);

        // Call the method using reflection
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String jwt = (String) parseJwtMethod.invoke(authTokenFilter, request);

        // Verify that the token was extracted correctly
        assertEquals(TOKEN, jwt);
    }

    @Test
    void testParseJwt_WithInvalidHeader() throws Exception {
        // Mock request to return an invalid Authorization header
        when(request.getHeader("Authorization")).thenReturn("Invalid header");

        // Call the method using reflection
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String jwt = (String) parseJwtMethod.invoke(authTokenFilter, request);

        // Verify that no token was extracted
        assertNull(jwt);
    }

    @Test
    void testParseJwt_WithNullHeader() throws Exception {
        // Mock request to return a null Authorization header
        when(request.getHeader("Authorization")).thenReturn(null);

        // Call the method using reflection
        java.lang.reflect.Method parseJwtMethod = AuthTokenFilter.class.getDeclaredMethod("parseJwt", HttpServletRequest.class);
        parseJwtMethod.setAccessible(true);
        String jwt = (String) parseJwtMethod.invoke(authTokenFilter, request);

        // Verify that no token was extracted
        assertNull(jwt);
    }
}