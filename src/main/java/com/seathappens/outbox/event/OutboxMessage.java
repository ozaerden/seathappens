package com.seathappens.outbox.event;

import java.util.UUID;

public record OutboxMessage(
        UUID eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        String correlationId,
        String payload
) {
}
