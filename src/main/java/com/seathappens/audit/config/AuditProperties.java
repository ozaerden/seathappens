package com.seathappens.audit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seathappens.audit")
public record AuditProperties(
        String topicName,
        String consumerGroup
) {
}
