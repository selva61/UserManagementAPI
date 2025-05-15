package com.circuit.usermanagementapi.repository;

import com.circuit.usermanagementapi.model.Team;
import com.circuit.usermanagementapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TeamRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void testFindByName() {
        // Create and persist a team
        Team team = new Team("Development Team", "Main development team");
        entityManager.persist(team);
        entityManager.flush();

        // Find the team by name
        Optional<Team> found = teamRepository.findByName("Development Team");

        // Assert that the team was found
        assertTrue(found.isPresent());
        assertEquals("Development Team", found.get().getName());
        assertEquals("Main development team", found.get().getDescription());
    }

    @Test
    void testFindByName_NotFound() {
        // Find a non-existent team
        Optional<Team> found = teamRepository.findByName("Nonexistent Team");

        // Assert that the team was not found
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByName() {
        // Create and persist a team
        Team team = new Team("Development Team", "Main development team");
        entityManager.persist(team);
        entityManager.flush();

        // Check if the team exists by name
        Boolean exists = teamRepository.existsByName("Development Team");

        // Assert that the team exists
        assertTrue(exists);
    }

    @Test
    void testExistsByName_NotFound() {
        // Check if a non-existent team exists
        Boolean exists = teamRepository.existsByName("Nonexistent Team");

        // Assert that the team does not exist
        assertFalse(exists);
    }

    @Test
    void testSaveTeam() {
        // Create a new team
        Team team = new Team("Development Team", "Main development team");
        
        // Add a member to the team
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persist(user);
        team.addMember(user);
        
        // Save the team
        Team savedTeam = teamRepository.save(team);
        
        // Assert that the team was saved with an ID
        assertNotNull(savedTeam.getId());
        assertEquals("Development Team", savedTeam.getName());
        assertEquals("Main development team", savedTeam.getDescription());
        assertEquals(1, savedTeam.getMembers().size());
    }
    
    @Test
    void testDeleteTeam() {
        // Create and persist a team
        Team team = new Team("Development Team", "Main development team");
        entityManager.persist(team);
        entityManager.flush();
        
        // Get the team ID
        Long teamId = team.getId();
        
        // Delete the team
        teamRepository.delete(team);
        entityManager.flush();
        
        // Try to find the team by ID
        Optional<Team> foundTeam = teamRepository.findById(teamId);
        
        // Assert that the team was deleted
        assertFalse(foundTeam.isPresent());
    }
    
    @Test
    void testTeamWithMembers() {
        // Create a team
        Team team = new Team("Development Team", "Main development team");
        
        // Create and add users to the team
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");
        
        entityManager.persist(user1);
        entityManager.persist(user2);
        
        team.addMember(user1);
        team.addMember(user2);
        
        // Save the team
        Team savedTeam = teamRepository.save(team);
        entityManager.flush();
        
        // Find the team by ID
        Optional<Team> foundTeam = teamRepository.findById(savedTeam.getId());
        
        // Assert that the team was found with its members
        assertTrue(foundTeam.isPresent());
        assertEquals(2, foundTeam.get().getMembers().size());
    }
}