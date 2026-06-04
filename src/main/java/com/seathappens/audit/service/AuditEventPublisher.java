package com.seathappens.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seathappens.audit.config.AuditProperties;
import com.seathappens.audit.event.AuditAction;
import com.seathappens.audit.event.AuditEvent;
import com.seathappens.common.context.CorrelationIdContext;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventPublisher {

    private static final ZoneId ISTANBUL_ZONE = ZoneId.of("Europe/Istanbul");

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AuditProperties auditProperties;

    public void publish(
            AuditAction action,
            UUID actorUserId,
            String entityType,
            String entityId,
            Map<String, String> metadata
    ) {
        AuditEvent event = new AuditEvent(
                UUID.randomUUID(),
                action,
                actorUserId,
                entityType,
                entityId,
                CorrelationIdContext.getCurrentCorrelationId().orElse(null),
                ZonedDateTime.now(ISTANBUL_ZONE).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                metadata
        );

        publishAfterCommit(event);
    }

    private void publishAfterCommit(AuditEvent event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publishImmediately(event);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publishImmediately(event);
            }
        });
    }

    private void publishImmediately(AuditEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    auditProperties.topicName(),
                    event.entityId(),
                    message
            );

            log.info(
                    "Published audit event. eventId={}, action={}, entityType={}, entityId={}, correlationId={}",
                    event.eventId(),
                    event.action(),
                    event.entityType(),
                    event.entityId(),
                    event.correlationId()
            );
        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(ErrorCode.AUDIT_SERIALIZATION_ERROR, exception);
        }
    }
}
