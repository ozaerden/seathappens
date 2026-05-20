package com.seathappens.outbox.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seathappens.outbox")
public record OutboxProperties(
        long publisherFixedRateMs,
        String topicName,
        int maxRetryCount,
        long retryDelayMs
) {
}
