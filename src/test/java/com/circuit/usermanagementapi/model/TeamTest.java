package com.circuit.usermanagementapi.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    private Team team;
    private final String TEAM_NAME = "Test Team";
    private final String TEAM_DESCRIPTION = "Test Team Description";

    @BeforeEach
    void setUp() {
        team = new Team(TEAM_NAME, TEAM_DESCRIPTION);
    }

    @Test
    void testTeamConstructors() {
        // Test default constructor
        Team emptyTeam = new Team();
        assertNotNull(emptyTeam);
        assertNull(emptyTeam.getName());
        assertNull(emptyTeam.getDescription());
        
        // Test parameterized constructor
        assertEquals(TEAM_NAME, team.getName());
        assertEquals(TEAM_DESCRIPTION, team.getDescription());
    }

    @Test
    void testIdGetterAndSetter() {
        // Initial id should be null
        assertNull(team.getId());
        
        // Set id
        Long id = 1L;
        team.setId(id);
        assertEquals(id, team.getId());
    }

    @Test
    void testNameGetterAndSetter() {
        // Initial name should be set in constructor
        assertEquals(TEAM_NAME, team.getName());
        
        // Set name
        String newName = "New Team Name";
        team.setName(newName);
        assertEquals(newName, team.getName());
    }

    @Test
    void testDescriptionGetterAndSetter() {
        // Initial description should be set in constructor
        assertEquals(TEAM_DESCRIPTION, team.getDescription());
        
        // Set description
        String newDescription = "New Team Description";
        team.setDescription(newDescription);
        assertEquals(newDescription, team.getDescription());
    }

    @Test
    void testMembersGetterAndSetter() {
        // Initial members should be empty but not null
        assertNotNull(team.getMembers());
        assertTrue(team.getMembers().isEmpty());
        
        // Set members
        Set<User> members = new HashSet<>();
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");
        members.add(user1);
        members.add(user2);
        
        team.setMembers(members);
        assertEquals(2, team.getMembers().size());
        assertTrue(team.getMembers().contains(user1));
        assertTrue(team.getMembers().contains(user2));
    }

    @Test
    void testAddMember() {
        // Initial members should be empty
        assertTrue(team.getMembers().isEmpty());
        
        // Add a member
        User user = new User("user", "user@example.com", "password");
        team.addMember(user);
        
        // Check that the member was added to the team
        assertEquals(1, team.getMembers().size());
        assertTrue(team.getMembers().contains(user));
        
        // Check that the team was set on the user
        assertEquals(team, user.getTeam());
    }

    @Test
    void testRemoveMember() {
        // Add members
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");
        team.addMember(user1);
        team.addMember(user2);
        assertEquals(2, team.getMembers().size());
        
        // Remove a member
        team.removeMember(user1);
        
        // Check that the member was removed from the team
        assertEquals(1, team.getMembers().size());
        assertFalse(team.getMembers().contains(user1));
        assertTrue(team.getMembers().contains(user2));
        
        // Check that the team was unset on the user
        assertNull(user1.getTeam());
        assertEquals(team, user2.getTeam());
    }
}