package com.circuit.usermanagementapi.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    private DatabaseHealthIndicator databaseHealthIndicator;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        databaseHealthIndicator = new DatabaseHealthIndicator(jdbcTemplate);
    }

    @Test
    void testHealthUp() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        // Act
        Health health = databaseHealthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("PostgreSQL", health.getDetails().get("database"));
        assertEquals("Available", health.getDetails().get("status"));
    }

    @Test
    void testHealthDownUnexpectedValue() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(0);

        // Act
        Health health = databaseHealthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PostgreSQL", health.getDetails().get("database"));
        assertEquals("Unexpected value: 0", health.getDetails().get("status"));
    }

    @Test
    void testHealthDownException() {
        // Arrange
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        Health health = databaseHealthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PostgreSQL", health.getDetails().get("database"));
        assertEquals("Unavailable", health.getDetails().get("status"));
        assertEquals("Database connection failed", health.getDetails().get("error"));
    }
}