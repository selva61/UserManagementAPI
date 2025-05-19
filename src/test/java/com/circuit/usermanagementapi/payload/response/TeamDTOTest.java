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

class TeamDTOTest {

    private Team team;
    private User user1;
    private User user2;
    private final Long TEAM_ID = 1L;
    private final String TEAM_NAME = "Test Team";
    private final String TEAM_DESCRIPTION = "Test Team Description";
    private final Long USER1_ID = 1L;
    private final String USER1_USERNAME = "testuser1";
    private final String USER1_EMAIL = "test1@example.com";
    private final String USER1_PASSWORD = "password123";
    private final Long USER2_ID = 2L;
    private final String USER2_USERNAME = "testuser2";
    private final String USER2_EMAIL = "test2@example.com";
    private final String USER2_PASSWORD = "password456";

    @BeforeEach
    void setUp() {
        // Create team
        team = new Team(TEAM_NAME, TEAM_DESCRIPTION);
        team.setId(TEAM_ID);

        // Create users
        user1 = new User(USER1_USERNAME, USER1_EMAIL, USER1_PASSWORD);
        user1.setId(USER1_ID);
        user1.setFirstName("Test");
        user1.setLastName("User1");

        user2 = new User(USER2_USERNAME, USER2_EMAIL, USER2_PASSWORD);
        user2.setId(USER2_ID);
        user2.setFirstName("Test");
        user2.setLastName("User2");

        // Set up roles
        Set<Role> roles = new HashSet<>();
        Role role = new Role(ERole.ROLE_TEAM_MEMBER);
        role.setId(1L);
        roles.add(role);
        user1.setRoles(roles);
        user2.setRoles(roles);

        // Add users to team
        Set<User> members = new HashSet<>();
        members.add(user1);
        members.add(user2);
        team.setMembers(members);
        user1.setTeam(team);
        user2.setTeam(team);
    }

    @Test
    void testTeamDTOConstructorWithTeam() {
        // Create TeamDTO from Team
        TeamDTO teamDTO = new TeamDTO(team);

        // Assert that the fields are correctly set
        assertEquals(TEAM_ID, teamDTO.getId());
        assertEquals(TEAM_NAME, teamDTO.getName());
        assertEquals(TEAM_DESCRIPTION, teamDTO.getDescription());
        assertEquals(2, teamDTO.getMembers().size());

        // Verify that the members are converted to UserDTOs
        for (UserDTO userDTO : teamDTO.getMembers()) {
            if (userDTO.getId().equals(USER1_ID)) {
                assertEquals(USER1_USERNAME, userDTO.getUsername());
                // Verify that sensitive data is not included
                assertFalse(userDTO.toString().contains(USER1_PASSWORD));
                assertFalse(userDTO.toString().contains(USER1_EMAIL));
            } else if (userDTO.getId().equals(USER2_ID)) {
                assertEquals(USER2_USERNAME, userDTO.getUsername());
                // Verify that sensitive data is not included
                assertFalse(userDTO.toString().contains(USER2_PASSWORD));
                assertFalse(userDTO.toString().contains(USER2_EMAIL));
            } else {
                fail("Unexpected user ID: " + userDTO.getId());
            }
        }
    }

    @Test
    void testTeamDTOConstructorWithTeamWithNullMembers() {
        // Create team with null members
        Team teamWithNullMembers = new Team(TEAM_NAME, TEAM_DESCRIPTION);
        teamWithNullMembers.setId(TEAM_ID);
        teamWithNullMembers.setMembers(null);

        // Create TeamDTO from Team
        TeamDTO teamDTO = new TeamDTO(teamWithNullMembers);

        // Assert that the fields are correctly set
        assertEquals(TEAM_ID, teamDTO.getId());
        assertEquals(TEAM_NAME, teamDTO.getName());
        assertEquals(TEAM_DESCRIPTION, teamDTO.getDescription());
        assertTrue(teamDTO.getMembers().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        // Create empty TeamDTO
        TeamDTO teamDTO = new TeamDTO();

        // Set fields
        teamDTO.setId(TEAM_ID);
        teamDTO.setName(TEAM_NAME);
        teamDTO.setDescription(TEAM_DESCRIPTION);
        
        Set<UserDTO> members = new HashSet<>();
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(USER1_ID);
        userDTO1.setUsername(USER1_USERNAME);
        members.add(userDTO1);
        teamDTO.setMembers(members);

        // Assert that the fields are correctly set
        assertEquals(TEAM_ID, teamDTO.getId());
        assertEquals(TEAM_NAME, teamDTO.getName());
        assertEquals(TEAM_DESCRIPTION, teamDTO.getDescription());
        assertEquals(1, teamDTO.getMembers().size());
        assertTrue(teamDTO.getMembers().stream().anyMatch(u -> u.getId().equals(USER1_ID)));
    }
}