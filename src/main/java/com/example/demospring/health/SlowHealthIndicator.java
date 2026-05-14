package com.example.demospring.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component("slow")
public class SlowHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {

       try {
            Thread.sleep(5000); // 5 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Health.up()
                .withDetail("delay", "5 segundos")
                .build();
    }
}