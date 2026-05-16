package com.seathappens.reservation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seathappens.reservation")
public record ReservationProperties(
        int durationMinutes
) {
}
