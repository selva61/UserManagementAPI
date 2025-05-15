package com.circuit.usermanagementapi.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationHealthIndicatorTest {

    @Test
    void testHealthUp() {
        // Arrange
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator();

        // Act
        Health health = applicationHealthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("Application is running normally", health.getDetails().get("status"));
        assertNotNull(health.getDetails().get("startTime"));
        assertNotNull(health.getDetails().get("uptime"));
    }

    @Test
    void testUptimeFormat() {
        // Arrange
        ApplicationHealthIndicator applicationHealthIndicator = new ApplicationHealthIndicator();

        // Act
        Health health = applicationHealthIndicator.health();
        String uptime = (String) health.getDetails().get("uptime");

        // Assert
        assertNotNull(uptime);
        assertTrue(uptime.matches("\\d+ days, \\d+ hours, \\d+ minutes, \\d+ seconds"), 
                "Uptime format should be 'X days, Y hours, Z minutes, W seconds'");
    }
}