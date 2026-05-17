package com.seathappens.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seathappens.order")
public record OrderProperties(
        long expirySchedulerFixedRateMs
) {
}
