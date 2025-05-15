package com.circuit.usermanagementapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String FIRST_NAME = "Test";
    private final String LAST_NAME = "User";

    @BeforeEach
    void setUp() {
        user = new User(USERNAME, EMAIL, PASSWORD);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
    }

    @Test
    void testUserConstructor() {
        // Test default constructor
        User emptyUser = new User();
        assertNotNull(emptyUser);
        
        // Test parameterized constructor
        assertEquals(USERNAME, user.getUsername());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    void testIdGetterAndSetter() {
        Long id = 1L;
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    void testUsernameGetterAndSetter() {
        String newUsername = "newusername";
        user.setUsername(newUsername);
        assertEquals(newUsername, user.getUsername());
    }

    @Test
    void testEmailGetterAndSetter() {
        String newEmail = "newemail@example.com";
        user.setEmail(newEmail);
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void testPasswordGetterAndSetter() {
        String newPassword = "newpassword123";
        user.setPassword(newPassword);
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void testFirstNameGetterAndSetter() {
        assertEquals(FIRST_NAME, user.getFirstName());
        
        String newFirstName = "NewFirstName";
        user.setFirstName(newFirstName);
        assertEquals(newFirstName, user.getFirstName());
    }

    @Test
    void testLastNameGetterAndSetter() {
        assertEquals(LAST_NAME, user.getLastName());
        
        String newLastName = "NewLastName";
        user.setLastName(newLastName);
        assertEquals(newLastName, user.getLastName());
    }

    @Test
    void testRolesGetterAndSetter() {
        // Initial roles should be empty but not null
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
        
        // Set roles
        Set<Role> roles = new HashSet<>();
        Role role1 = new Role(ERole.ROLE_TEAM_MEMBER);
        Role role2 = new Role(ERole.ROLE_SCRUM_MASTER);
        roles.add(role1);
        roles.add(role2);
        
        user.setRoles(roles);
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
        assertTrue(user.getRoles().contains(role2));
    }

    @Test
    void testTeamGetterAndSetter() {
        // Initial team should be null
        assertNull(user.getTeam());
        
        // Set team
        Team team = new Team("TestTeam", "Test Team Description");
        user.setTeam(team);
        assertEquals(team, user.getTeam());
    }

    @Test
    void testPreferencesGetterAndSetter() {
        // Initial preferences should be empty but not null
        assertNotNull(user.getPreferences());
        assertTrue(user.getPreferences().isEmpty());
        
        // Set preferences
        Map<String, String> preferences = new HashMap<>();
        preferences.put("theme", "dark");
        preferences.put("language", "en");
        
        user.setPreferences(preferences);
        assertEquals(2, user.getPreferences().size());
        assertEquals("dark", user.getPreferences().get("theme"));
        assertEquals("en", user.getPreferences().get("language"));
    }

    @Test
    void testAddPreference() {
        user.addPreference("theme", "dark");
        assertEquals("dark", user.getPreference("theme"));
        
        // Add another preference
        user.addPreference("language", "en");
        assertEquals(2, user.getPreferences().size());
        assertEquals("en", user.getPreference("language"));
        
        // Override existing preference
        user.addPreference("theme", "light");
        assertEquals("light", user.getPreference("theme"));
    }

    @Test
    void testGetPreference() {
        // Get non-existent preference
        assertNull(user.getPreference("nonexistent"));
        
        // Add and get preference
        user.addPreference("theme", "dark");
        assertEquals("dark", user.getPreference("theme"));
    }

    @Test
    void testRemovePreference() {
        // Add preferences
        user.addPreference("theme", "dark");
        user.addPreference("language", "en");
        assertEquals(2, user.getPreferences().size());
        
        // Remove preference
        user.removePreference("theme");
        assertEquals(1, user.getPreferences().size());
        assertNull(user.getPreference("theme"));
        assertEquals("en", user.getPreference("language"));
        
        // Remove non-existent preference (should not throw exception)
        user.removePreference("nonexistent");
        assertEquals(1, user.getPreferences().size());
    }
}