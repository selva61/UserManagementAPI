package com.circuit.usermanagementapi.payload.response;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

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
    }

    @Test
    void testUserDTOConstructorWithUser() {
        // Create UserDTO from User
        UserDTO userDTO = new UserDTO(user);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDTO.getId());
        assertEquals(USERNAME, userDTO.getUsername());
        assertEquals(FIRST_NAME, userDTO.getFirstName());
        assertEquals(LAST_NAME, userDTO.getLastName());
        assertEquals(TEAM_ID, userDTO.getTeamId());
        assertEquals(TEAM_NAME, userDTO.getTeamName());
        assertTrue(userDTO.getRoles().contains("ROLE_TEAM_MEMBER"));
    }

    @Test
    void testUserDTOConstructorWithUserWithNullTeam() {
        // Create user with null team
        User userWithNullTeam = new User(USERNAME, EMAIL, "password123");
        userWithNullTeam.setId(USER_ID);
        userWithNullTeam.setFirstName(FIRST_NAME);
        userWithNullTeam.setLastName(LAST_NAME);
        userWithNullTeam.setTeam(null);
        userWithNullTeam.setRoles(user.getRoles());

        // Create UserDTO from User
        UserDTO userDTO = new UserDTO(userWithNullTeam);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDTO.getId());
        assertEquals(USERNAME, userDTO.getUsername());
        assertEquals(FIRST_NAME, userDTO.getFirstName());
        assertEquals(LAST_NAME, userDTO.getLastName());
        assertNull(userDTO.getTeamId());
        assertNull(userDTO.getTeamName());
        assertTrue(userDTO.getRoles().contains("ROLE_TEAM_MEMBER"));
    }

    @Test
    void testUserDTOConstructorWithUserWithNullRoles() {
        // Create user with null roles
        User userWithNullRoles = new User(USERNAME, EMAIL, "password123");
        userWithNullRoles.setId(USER_ID);
        userWithNullRoles.setFirstName(FIRST_NAME);
        userWithNullRoles.setLastName(LAST_NAME);
        userWithNullRoles.setTeam(team);
        userWithNullRoles.setRoles(null);

        // Create UserDTO from User
        UserDTO userDTO = new UserDTO(userWithNullRoles);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDTO.getId());
        assertEquals(USERNAME, userDTO.getUsername());
        assertEquals(FIRST_NAME, userDTO.getFirstName());
        assertEquals(LAST_NAME, userDTO.getLastName());
        assertEquals(TEAM_ID, userDTO.getTeamId());
        assertEquals(TEAM_NAME, userDTO.getTeamName());
        assertNull(userDTO.getRoles());
    }

    @Test
    void testSettersAndGetters() {
        // Create empty UserDTO
        UserDTO userDTO = new UserDTO();

        // Set fields
        userDTO.setId(USER_ID);
        userDTO.setUsername(USERNAME);
        userDTO.setFirstName(FIRST_NAME);
        userDTO.setLastName(LAST_NAME);
        userDTO.setTeamId(TEAM_ID);
        userDTO.setTeamName(TEAM_NAME);
        
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_TEAM_MEMBER");
        userDTO.setRoles(roles);

        // Assert that the fields are correctly set
        assertEquals(USER_ID, userDTO.getId());
        assertEquals(USERNAME, userDTO.getUsername());
        assertEquals(FIRST_NAME, userDTO.getFirstName());
        assertEquals(LAST_NAME, userDTO.getLastName());
        assertEquals(TEAM_ID, userDTO.getTeamId());
        assertEquals(TEAM_NAME, userDTO.getTeamName());
        assertTrue(userDTO.getRoles().contains("ROLE_TEAM_MEMBER"));
    }
}