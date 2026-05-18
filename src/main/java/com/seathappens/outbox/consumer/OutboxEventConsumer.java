package com.seathappens.outbox.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OutboxEventConsumer {

    @KafkaListener(
            topics = "${seathappens.outbox.topic-name}",
            groupId = "seathappens-local-consumer"
    )
    public void consume(String message) {
        log.info("Consumed Kafka event: {}", message);
    }

}
