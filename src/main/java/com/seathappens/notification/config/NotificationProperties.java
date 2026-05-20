package com.seathappens.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seathappens.notification")
public record NotificationProperties(
        String defaultRecipient
) {
}
