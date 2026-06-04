package com.seathappens.audit.event;

import java.util.Map;
import java.util.UUID;

public record AuditEvent(
        UUID eventId,
        AuditAction action,
        UUID actorUserId,
        String entityType,
        String entityId,
        String correlationId,
        String occurredAt,
        Map<String, String> metadata
) {
}
