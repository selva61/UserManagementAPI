package com.circuit.usermanagementapi.repository;

import com.circuit.usermanagementapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        // Create and persist a user
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();

        // Find the user by username
        Optional<User> found = userRepository.findByUsername("testuser");

        // Assert that the user was found
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByUsername_NotFound() {
        // Find a non-existent user
        Optional<User> found = userRepository.findByUsername("nonexistentuser");

        // Assert that the user was not found
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail() {
        // Create and persist a user
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();

        // Find the user by email
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert that the user was found
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Find a non-existent user
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Assert that the user was not found
        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByUsername() {
        // Create and persist a user
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();

        // Check if the user exists by username
        Boolean exists = userRepository.existsByUsername("testuser");

        // Assert that the user exists
        assertTrue(exists);
    }

    @Test
    void testExistsByUsername_NotFound() {
        // Check if a non-existent user exists
        Boolean exists = userRepository.existsByUsername("nonexistentuser");

        // Assert that the user does not exist
        assertFalse(exists);
    }

    @Test
    void testExistsByEmail() {
        // Create and persist a user
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persist(user);
        entityManager.flush();

        // Check if the user exists by email
        Boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert that the user exists
        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_NotFound() {
        // Check if a non-existent user exists
        Boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert that the user does not exist
        assertFalse(exists);
    }
}