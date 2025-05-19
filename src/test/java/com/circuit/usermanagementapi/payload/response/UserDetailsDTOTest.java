package com.circuit.usermanagementapi.payload.response;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsDTOTest {

    private User user;
    private Team team;
    private final Long USER_ID = 1L;
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String FIRST_NAME = "Test";
    private final String LAST_NAME = "User";
    private final Long TEAM_ID = 1L;
    private final String TEAM_NAME = "Test Team";

    @BeforeEach
    void setUp() {
        // Create team
        team = new Team(TEAM_NAME, "Test Team Description");
        team.setId(TEAM_ID);

        // Create user
        user = new User(USERNAME, EMAIL, "password123");
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setTeam(team);

        // Set up roles
        Set<Role> roles = new HashSet<>();
        Role role = new Role(ERole.ROLE_TEAM_MEMBER);
        role.setId(1L);
        roles.add(role);
        user.setRoles(roles);

        // Set up preferences
        Map<String, String> preferences = new HashMap<>();
        preferences.put("theme", "dark");
        preferences.put("language", "en");
        user.setPreferences(preferences);
    }

    @Test
    void testUserDetailsDTOConstructorWithUser() {
        // Create UserDetailsDTO from User
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(user);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDetailsDTO.getId());
        assertEquals(USERNAME, userDetailsDTO.getUsername());
        assertEquals(EMAIL, userDetailsDTO.getEmail());
        assertEquals(FIRST_NAME, userDetailsDTO.getFirstName());
        assertEquals(LAST_NAME, userDetailsDTO.getLastName());
        assertEquals(TEAM_ID, userDetailsDTO.getTeamId());
        assertEquals(TEAM_NAME, userDetailsDTO.getTeamName());
        assertTrue(userDetailsDTO.getRoles().contains("ROLE_TEAM_MEMBER"));
        assertEquals(2, userDetailsDTO.getPreferences().size());
        assertEquals("dark", userDetailsDTO.getPreferences().get("theme"));
        assertEquals("en", userDetailsDTO.getPreferences().get("language"));
    }

    @Test
    void testUserDetailsDTOConstructorWithUserWithNullPreferences() {
        // Create user with null preferences
        User userWithNullPreferences = new User(USERNAME, EMAIL, "password123");
        userWithNullPreferences.setId(USER_ID);
        userWithNullPreferences.setFirstName(FIRST_NAME);
        userWithNullPreferences.setLastName(LAST_NAME);
        userWithNullPreferences.setTeam(team);
        userWithNullPreferences.setRoles(user.getRoles());
        userWithNullPreferences.setPreferences(null);

        // Create UserDetailsDTO from User
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(userWithNullPreferences);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDetailsDTO.getId());
        assertEquals(USERNAME, userDetailsDTO.getUsername());
        assertEquals(EMAIL, userDetailsDTO.getEmail());
        assertEquals(FIRST_NAME, userDetailsDTO.getFirstName());
        assertEquals(LAST_NAME, userDetailsDTO.getLastName());
        assertEquals(TEAM_ID, userDetailsDTO.getTeamId());
        assertEquals(TEAM_NAME, userDetailsDTO.getTeamName());
        assertTrue(userDetailsDTO.getRoles().contains("ROLE_TEAM_MEMBER"));
        assertNull(userDetailsDTO.getPreferences());
    }

    @Test
    void testSettersAndGetters() {
        // Create empty UserDetailsDTO
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();

        // Set fields
        userDetailsDTO.setId(USER_ID);
        userDetailsDTO.setUsername(USERNAME);
        userDetailsDTO.setEmail(EMAIL);
        userDetailsDTO.setFirstName(FIRST_NAME);
        userDetailsDTO.setLastName(LAST_NAME);
        userDetailsDTO.setTeamId(TEAM_ID);
        userDetailsDTO.setTeamName(TEAM_NAME);
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_TEAM_MEMBER");
        userDetailsDTO.setRoles(roles);
        
        Map<String, String> preferences = new HashMap<>();
        preferences.put("theme", "dark");
        preferences.put("language", "en");
        userDetailsDTO.setPreferences(preferences);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDetailsDTO.getId());
        assertEquals(USERNAME, userDetailsDTO.getUsername());
        assertEquals(EMAIL, userDetailsDTO.getEmail());
        assertEquals(FIRST_NAME, userDetailsDTO.getFirstName());
        assertEquals(LAST_NAME, userDetailsDTO.getLastName());
        assertEquals(TEAM_ID, userDetailsDTO.getTeamId());
        assertEquals(TEAM_NAME, userDetailsDTO.getTeamName());
        assertTrue(userDetailsDTO.getRoles().contains("ROLE_TEAM_MEMBER"));
        assertEquals(2, userDetailsDTO.getPreferences().size());
        assertEquals("dark", userDetailsDTO.getPreferences().get("theme"));
        assertEquals("en", userDetailsDTO.getPreferences().get("language"));
    }
}