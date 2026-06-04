package com.seathappens.audit.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seathappens.audit.entity.AuditLogDocument;
import com.seathappens.audit.event.AuditEvent;
import com.seathappens.audit.repository.AuditLogRepository;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final ObjectMapper objectMapper;
    private final AuditLogRepository auditLogRepository;

    @KafkaListener(
            topics = "${seathappens.audit.topic-name}",
            groupId = "${seathappens.audit.consumer-group}"
    )
    public void consume(String message) {
        AuditEvent event = deserialize(message);
        String documentId = event.eventId().toString();

        if (auditLogRepository.existsById(documentId)) {
            log.info(
                    "Skipping already processed audit event. eventId={}, action={}, correlationId={}",
                    event.eventId(),
                    event.action(),
                    event.correlationId()
            );
            return;
        }

        auditLogRepository.save(AuditLogDocument.builder()
                .id(documentId)
                .action(event.action())
                .actorUserId(event.actorUserId() == null ? null : event.actorUserId().toString())
                .entityType(event.entityType())
                .entityId(event.entityId())
                .correlationId(event.correlationId())
                .occurredAt(event.occurredAt())
                .metadata(event.metadata())
                .build());

        log.info(
                "Stored audit event. eventId={}, action={}, entityType={}, entityId={}, correlationId={}",
                event.eventId(),
                event.action(),
                event.entityType(),
                event.entityId(),
                event.correlationId()
        );
    }

    private AuditEvent deserialize(String message) {
        try {
            return objectMapper.readValue(message, AuditEvent.class);
        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(ErrorCode.AUDIT_SERIALIZATION_ERROR, exception);
        }
    }
}
