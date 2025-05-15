package com.circuit.usermanagementapi.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom health indicator that provides overall application health status.
 * This indicator will be included in the /actuator/health endpoint.
 */
@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    private final LocalDateTime startTime = LocalDateTime.now();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Health health() {
        return Health.up()
                .withDetail("status", "Application is running normally")
                .withDetail("startTime", startTime.format(formatter))
                .withDetail("uptime", getUptime())
                .build();
    }

    private String getUptime() {
        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(startTime, now).getSeconds();
        long days = seconds / (24 * 3600);
        seconds = seconds % (24 * 3600);
        long hours = seconds / 3600;
        seconds = seconds % 3600;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }
}