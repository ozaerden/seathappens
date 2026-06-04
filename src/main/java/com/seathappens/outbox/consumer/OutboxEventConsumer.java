package com.seathappens.outbox.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import com.seathappens.notification.service.NotificationService;
import com.seathappens.outbox.config.OutboxProperties;
import com.seathappens.outbox.entity.ProcessedKafkaEvent;
import com.seathappens.outbox.event.OutboxMessage;
import com.seathappens.outbox.repository.ProcessedKafkaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventConsumer {

    private static final String CONSUMER_GROUP = "seathappens-local-consumer";

    private final ObjectMapper objectMapper;
    private final OutboxProperties outboxProperties;
    private final ProcessedKafkaEventRepository processedKafkaEventRepository;
    private final NotificationService notificationService;

    @Transactional
    @KafkaListener(
            topics = "${seathappens.outbox.topic-name}",
            groupId = CONSUMER_GROUP
    )
    public void consume(String message) {
        OutboxMessage outboxMessage = deserialize(message);

        if (processedKafkaEventRepository.existsByEventId(outboxMessage.eventId())) {
            log.info(
                    "Skipping already processed Kafka event. eventId={}, eventType={}, correlationId={}",
                    outboxMessage.eventId(),
                    outboxMessage.eventType(),
                    outboxMessage.correlationId()
            );
            return;
        }

        log.info(
                "Consumed Kafka event. eventId={}, eventType={}, correlationId={}, payload={}",
                outboxMessage.eventId(),
                outboxMessage.eventType(),
                outboxMessage.correlationId(),
                outboxMessage.payload()
        );

        if ("PAYMENT_SUCCEEDED".equals(outboxMessage.eventType())) {
            notificationService.createPaymentSucceededNotification(
                    outboxMessage.eventId(),
                    outboxMessage.eventType(),
                    outboxMessage.payload()
            );
        }

        processedKafkaEventRepository.save(ProcessedKafkaEvent.builder()
                .eventId(outboxMessage.eventId())
                .eventType(outboxMessage.eventType())
                .topic(outboxProperties.topicName())
                .consumerGroup(CONSUMER_GROUP)
                .processedAt(LocalDateTime.now())
                .build());
    }

    private OutboxMessage deserialize(String message) {
        try {
            return objectMapper.readValue(message, OutboxMessage.class);
        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(
                    ErrorCode.OUTBOX_SERIALIZATION_ERROR,
                    exception
            );
        }
    }

}
