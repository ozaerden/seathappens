package com.seathappens.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seathappens.security.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes
) {
}
