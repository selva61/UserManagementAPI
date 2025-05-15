package com.circuit.usermanagementapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLoggingFilterTest {

    private RequestLoggingFilter requestLoggingFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        requestLoggingFilter = new RequestLoggingFilter();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_BasicRequestLogging() throws Exception {
        // Arrange
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");
        mockRequest.setRequestURI("/api/users");
        mockRequest.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();

        // Act
        requestLoggingFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertNotNull(mockResponse.getHeader("X-Request-ID"));
        assertTrue(mockResponse.getHeader("X-Request-ID").length() > 0);
    }

    @Test
    void testDoFilterInternal_WithAuthenticatedUser() throws Exception {
        // Arrange
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");
        mockRequest.setRequestURI("/api/users");
        mockRequest.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();

        // Set up authenticated user
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        requestLoggingFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertNotNull(mockResponse.getHeader("X-Request-ID"));
        assertTrue(mockResponse.getHeader("X-Request-ID").length() > 0);
    }

    @Test
    void testDoFilterInternal_WithAnonymousUser() throws Exception {
        // Arrange
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");
        mockRequest.setRequestURI("/api/users");
        mockRequest.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain();

        // Set up anonymous user
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "anonymousUser",
                null,
                Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        requestLoggingFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);

        // Assert
        assertNotNull(mockResponse.getHeader("X-Request-ID"));
        assertTrue(mockResponse.getHeader("X-Request-ID").length() > 0);
    }

    @Test
    void testDoFilterInternal_ExceptionHandling() throws Exception {
        // Arrange
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");
        mockRequest.setRequestURI("/api/users");
        mockRequest.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        FilterChain mockFilterChain = mock(FilterChain.class);
        
        // Set up filter chain to throw exception
        doThrow(new RuntimeException("Test exception")).when(mockFilterChain).doFilter(any(), any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            requestLoggingFilter.doFilterInternal(mockRequest, mockResponse, mockFilterChain);
        });
        
        // Verify MDC is cleared even when exception occurs
        assertNull(MDC.get("requestId"));
    }
}