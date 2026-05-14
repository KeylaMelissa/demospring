package com.example.demospring.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/* 
@Component
public class SlowHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {

       try {
            Thread.sleep(70000); // 70 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Health.up()
                .withDetail("delay", "70 segundos")
                .build();
    }
}*/