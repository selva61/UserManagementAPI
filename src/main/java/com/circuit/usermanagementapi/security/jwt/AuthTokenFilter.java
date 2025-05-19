package com.circuit.usermanagementapi.security.jwt;

import com.circuit.usermanagementapi.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            logger.debug("Request URI: {}, Method: {}", request.getRequestURI(), request.getMethod());
            logger.debug("JWT token: {}", jwt != null ? "present" : "null");

            if (jwt != null) {
                logger.debug("JWT token length: {}", jwt.length());
                logger.debug("JWT token first 10 chars: {}", jwt.length() > 10 ? jwt.substring(0, 10) + "..." : jwt);

                boolean isValid = jwtUtils.validateJwtToken(jwt);
                logger.debug("JWT token valid: {}", isValid);

                if (isValid) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    logger.debug("Username from token: {}", username);

                    if (username != null) {
                        try {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                            logger.debug("User details loaded successfully for: {}", username);
                            logger.debug("User authorities: {}", userDetails.getAuthorities());

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("Authentication set in SecurityContextHolder");
                        } catch (Exception e) {
                            logger.error("Error loading user details for username: {}", username, e);
                        }
                    } else {
                        logger.error("Username extracted from token is null for request: {}", request.getRequestURI());
                    }
                } else {
                    logger.debug("JWT token validation failed for request: {}", request.getRequestURI());
                }
            } else {
                logger.debug("No JWT token found in request: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication for request: {}", request.getRequestURI(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", headerAuth != null ? 
                     (headerAuth.length() > 15 ? headerAuth.substring(0, 15) + "..." : headerAuth) : "null");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            logger.debug("Bearer token found in Authorization header");
            return headerAuth.substring(7);
        } else if (headerAuth != null) {
            logger.debug("Authorization header does not start with 'Bearer ' prefix");
        }

        // Check for token in request parameters as a fallback
        String paramToken = request.getParameter("token");
        if (StringUtils.hasText(paramToken)) {
            logger.debug("Token found in request parameter");
            return paramToken;
        }

        logger.debug("No JWT token found in request");
        return null;
    }
}
