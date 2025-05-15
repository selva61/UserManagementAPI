package com.circuit.usermanagementapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;
    private final ERole ROLE_NAME = ERole.ROLE_TEAM_MEMBER;

    @BeforeEach
    void setUp() {
        role = new Role(ROLE_NAME);
    }

    @Test
    void testRoleConstructors() {
        // Test default constructor
        Role emptyRole = new Role();
        assertNotNull(emptyRole);
        assertNull(emptyRole.getName());
        
        // Test parameterized constructor
        assertEquals(ROLE_NAME, role.getName());
    }

    @Test
    void testIdGetterAndSetter() {
        // Initial id should be null
        assertNull(role.getId());
        
        // Set id
        Long id = 1L;
        role.setId(id);
        assertEquals(id, role.getId());
    }

    @Test
    void testNameGetterAndSetter() {
        // Initial name should be set in constructor
        assertEquals(ROLE_NAME, role.getName());
        
        // Set name
        ERole newName = ERole.ROLE_SCRUM_MASTER;
        role.setName(newName);
        assertEquals(newName, role.getName());
    }

    @Test
    void testUsersGetterAndSetter() {
        // Initial users should be empty but not null
        assertNotNull(role.getUsers());
        assertTrue(role.getUsers().isEmpty());
        
        // Set users
        Set<User> users = new HashSet<>();
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");
        users.add(user1);
        users.add(user2);
        
        role.setUsers(users);
        assertEquals(2, role.getUsers().size());
        assertTrue(role.getUsers().contains(user1));
        assertTrue(role.getUsers().contains(user2));
    }
}