package com.circuit.usermanagementapi.security.services;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.User;
import com.circuit.usermanagementapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a user with roles
        user = new User(USERNAME, EMAIL, PASSWORD);
        user.setId(USER_ID);

        Set<Role> roles = new HashSet<>();
        Role role = new Role(ERole.ROLE_TEAM_MEMBER);
        roles.add(role);
        user.setRoles(roles);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Mock the repository to return the user
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // Call the service method
        UserDetails userDetails = userDetailsService.loadUserByUsername(USERNAME);

        // Assert that the returned UserDetails is not null and has the correct username
        assertNotNull(userDetails);
        assertEquals(USERNAME, userDetails.getUsername());

        // Assert that it's an instance of UserDetailsImpl
        assertTrue(userDetails instanceof UserDetailsImpl);

        // Cast to UserDetailsImpl and check additional properties
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertEquals(USER_ID, userDetailsImpl.getId());
        assertEquals(EMAIL, userDetailsImpl.getEmail());
        assertEquals(PASSWORD, userDetailsImpl.getPassword());
        assertEquals(1, userDetailsImpl.getAuthorities().size());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock the repository to return empty Optional
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentuser");
        });

        // Assert the exception message
        String expectedMessage = "User Not Found with username: nonexistentuser";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testLoadUserByUsername_OtherException() {
        // Mock the repository to throw a RuntimeException
        when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("Database error"));

        // Call the service method and expect a UsernameNotFoundException
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("testuser");
        });

        // Assert the exception message
        String expectedMessage = "Error loading user: Database error";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        // Assert that the cause is a RuntimeException
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("Database error", exception.getCause().getMessage());
    }
}
