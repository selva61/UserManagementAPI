package com.circuit.usermanagementapi.security.services;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private User user;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        user = new User(USERNAME, EMAIL, PASSWORD);
        user.setId(USER_ID);
        
        // Add roles to the user
        Set<Role> roles = new HashSet<>();
        Role role1 = new Role(ERole.ROLE_TEAM_MEMBER);
        Role role2 = new Role(ERole.ROLE_SCRUM_MASTER);
        roles.add(role1);
        roles.add(role2);
        user.setRoles(roles);
    }

    @Test
    void testBuild() {
        // Build UserDetailsImpl from User
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        // Assert that the properties were correctly mapped
        assertEquals(USER_ID, userDetails.getId());
        assertEquals(USERNAME, userDetails.getUsername());
        assertEquals(EMAIL, userDetails.getEmail());
        assertEquals(PASSWORD, userDetails.getPassword());
        
        // Assert that the authorities were correctly mapped
        assertEquals(2, userDetails.getAuthorities().size());
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        assertTrue(authorities.contains(ERole.ROLE_TEAM_MEMBER.name()));
        assertTrue(authorities.contains(ERole.ROLE_SCRUM_MASTER.name()));
    }

    @Test
    void testConstructor() {
        // Create authorities
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(ERole.ROLE_TEAM_MEMBER.name()),
                new SimpleGrantedAuthority(ERole.ROLE_SCRUM_MASTER.name())
        );
        
        // Create UserDetailsImpl using constructor
        UserDetailsImpl userDetails = new UserDetailsImpl(
                USER_ID, USERNAME, EMAIL, PASSWORD, authorities);
        
        // Assert that the properties were correctly set
        assertEquals(USER_ID, userDetails.getId());
        assertEquals(USERNAME, userDetails.getUsername());
        assertEquals(EMAIL, userDetails.getEmail());
        assertEquals(PASSWORD, userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    void testUserDetailsMethodsReturnTrue() {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        // All these methods should return true by default
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testEquals() {
        UserDetailsImpl userDetails1 = UserDetailsImpl.build(user);
        
        // Create a copy of the user with the same ID
        User userCopy = new User("differentname", "different@example.com", "differentpassword");
        userCopy.setId(USER_ID);
        UserDetailsImpl userDetails2 = UserDetailsImpl.build(userCopy);
        
        // Create a user with a different ID
        User differentUser = new User(USERNAME, EMAIL, PASSWORD);
        differentUser.setId(2L);
        UserDetailsImpl userDetails3 = UserDetailsImpl.build(differentUser);
        
        // Test equality
        assertEquals(userDetails1, userDetails1); // Same object
        assertEquals(userDetails1, userDetails2); // Different object, same ID
        assertNotEquals(userDetails1, userDetails3); // Different ID
        assertNotEquals(userDetails1, null); // Null
        assertNotEquals(userDetails1, new Object()); // Different class
    }
}