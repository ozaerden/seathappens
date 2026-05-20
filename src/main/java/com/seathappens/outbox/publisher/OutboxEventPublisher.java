package com.seathappens.outbox.publisher;

import com.seathappens.outbox.config.OutboxProperties;
import com.seathappens.outbox.entity.OutboxEvent;
import com.seathappens.outbox.entity.OutboxEventStatus;
import com.seathappens.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxProperties outboxProperties;

    @Scheduled(fixedRateString = "${seathappens.outbox.publisher-fixed-rate-ms}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findTop50ByStatusAndNextRetryAtIsNullOrStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        OutboxEventStatus.PENDING,
                        LocalDateTime.now()
                );

        for (OutboxEvent event : pendingEvents) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            kafkaTemplate.send(
                    outboxProperties.topicName(),
                    event.getAggregateId(),
                    event.getPayload()
            ).get();

            event.setStatus(OutboxEventStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
            event.setNextRetryAt(null);

            log.info(
                    "Published outbox event. eventId={}, eventType={}",
                    event.getId(),
                    event.getEventType()
            );

        } catch (Exception exception) {
            int nextRetryCount = event.getRetryCount() + 1;

            event.setRetryCount(nextRetryCount);
            event.setLastError(exception.getMessage());

            if (nextRetryCount >= outboxProperties.maxRetryCount()) {
                event.setStatus(OutboxEventStatus.FAILED);
                event.setNextRetryAt(null);

                log.error(
                        "Outbox event permanently failed. eventId={}, eventType={}, retryCount={}",
                        event.getId(),
                        event.getEventType(),
                        nextRetryCount,
                        exception
                );
            } else {
                event.setStatus(OutboxEventStatus.PENDING);
                event.setNextRetryAt(
                        LocalDateTime.now().plusNanos(outboxProperties.retryDelayMs() * 1_000_000)
                );

                log.warn(
                        "Outbox event publish failed. Scheduled retry. eventId={}, eventType={}, retryCount={}",
                        event.getId(),
                        event.getEventType(),
                        nextRetryCount,
                        exception
                );
            }
        }
    }

}
