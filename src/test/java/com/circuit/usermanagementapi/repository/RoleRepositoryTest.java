package com.circuit.usermanagementapi.repository;

import com.circuit.usermanagementapi.model.ERole;
import com.circuit.usermanagementapi.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByName() {
        // Create and persist roles
        Role scrumMasterRole = new Role(ERole.ROLE_SCRUM_MASTER);
        Role productOwnerRole = new Role(ERole.ROLE_PRODUCT_OWNER);
        Role teamMemberRole = new Role(ERole.ROLE_TEAM_MEMBER);
        
        entityManager.persist(scrumMasterRole);
        entityManager.persist(productOwnerRole);
        entityManager.persist(teamMemberRole);
        entityManager.flush();

        // Find roles by name
        Optional<Role> foundScrumMaster = roleRepository.findByName(ERole.ROLE_SCRUM_MASTER);
        Optional<Role> foundProductOwner = roleRepository.findByName(ERole.ROLE_PRODUCT_OWNER);
        Optional<Role> foundTeamMember = roleRepository.findByName(ERole.ROLE_TEAM_MEMBER);

        // Assert that the roles were found
        assertTrue(foundScrumMaster.isPresent());
        assertTrue(foundProductOwner.isPresent());
        assertTrue(foundTeamMember.isPresent());
        
        assertEquals(ERole.ROLE_SCRUM_MASTER, foundScrumMaster.get().getName());
        assertEquals(ERole.ROLE_PRODUCT_OWNER, foundProductOwner.get().getName());
        assertEquals(ERole.ROLE_TEAM_MEMBER, foundTeamMember.get().getName());
    }

    @Test
    void testFindByName_NotFound() {
        // Create and persist only one role
        Role teamMemberRole = new Role(ERole.ROLE_TEAM_MEMBER);
        entityManager.persist(teamMemberRole);
        entityManager.flush();

        // Try to find a role that doesn't exist
        Optional<Role> foundScrumMaster = roleRepository.findByName(ERole.ROLE_SCRUM_MASTER);

        // Assert that the role was not found
        assertFalse(foundScrumMaster.isPresent());
    }
    
    @Test
    void testSaveRole() {
        // Create a new role
        Role role = new Role(ERole.ROLE_SCRUM_MASTER);
        
        // Save the role
        Role savedRole = roleRepository.save(role);
        
        // Assert that the role was saved with an ID
        assertNotNull(savedRole.getId());
        assertEquals(ERole.ROLE_SCRUM_MASTER, savedRole.getName());
    }
    
    @Test
    void testDeleteRole() {
        // Create and persist a role
        Role role = new Role(ERole.ROLE_SCRUM_MASTER);
        entityManager.persist(role);
        entityManager.flush();
        
        // Get the role ID
        Long roleId = role.getId();
        
        // Delete the role
        roleRepository.delete(role);
        entityManager.flush();
        
        // Try to find the role by ID
        Optional<Role> foundRole = roleRepository.findById(roleId);
        
        // Assert that the role was deleted
        assertFalse(foundRole.isPresent());
    }
}