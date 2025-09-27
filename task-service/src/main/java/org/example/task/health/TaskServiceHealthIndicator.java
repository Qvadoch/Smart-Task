package org.example.task.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskServiceHealthIndicator implements HealthIndicator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            // Проверка подключения к БД
            jdbcTemplate.execute("SELECT 1 FROM DUAL");
            return Health.up()
                    .withDetail("database", "Connected")
                    .withDetail("service", "Task Service is operational")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("database", "Disconnected")
                    .build();
        }
    }
}