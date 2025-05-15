package com.circuit.usermanagementapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter for tracking and logging HTTP requests.
 * This filter generates a unique request ID for each request and logs request details.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString();
        
        // Wrap request and response to allow reading the body multiple times
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        // Add request ID to MDC for logging
        MDC.put(REQUEST_ID, requestId);
        
        // Add request ID to response headers
        responseWrapper.setHeader("X-Request-ID", requestId);
        
        // Get authenticated user if available
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            MDC.put(USER_ID, authentication.getName());
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Log request details
            logRequest(requestWrapper);
            
            // Continue with the filter chain
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // Log response details
            long duration = System.currentTimeMillis() - startTime;
            logResponse(responseWrapper, duration);
            
            // Copy content to the original response
            responseWrapper.copyBodyToResponse();
            
            // Clear MDC
            MDC.clear();
        }
    }
    
    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String userInfo = MDC.get(USER_ID) != null ? " (User: " + MDC.get(USER_ID) + ")" : " (Anonymous)";
        
        logger.info("Request: {} {} {} [RequestID: {}]{}",
                request.getMethod(),
                request.getRequestURI() + queryString,
                request.getRemoteAddr(),
                MDC.get(REQUEST_ID),
                userInfo);
    }
    
    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        logger.info("Response: {} ({} ms) [RequestID: {}]",
                response.getStatus(),
                duration,
                MDC.get(REQUEST_ID));
    }
}