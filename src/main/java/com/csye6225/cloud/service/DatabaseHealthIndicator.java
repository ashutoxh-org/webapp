package com.csye6225.cloud.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Database health indicator.
 */
@RequiredArgsConstructor
@Service
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    private final DataSource dataSource;

    /**
     * Database connectivity health check
     *
     * @return Health (Status.UP or Status.DOWN)
     */
    @Override
    public Health health() {
        LOGGER.info("Health check called");
        try (Connection ignored = dataSource.getConnection()) {
            return Health.up().build();
        } catch (SQLException e) {
            LOGGER.error("Exception while trying to connect to the database: {}", e.getMessage());
        }
        return Health.down().build();
    }

}
