package com.seathappens.outbox.entity;

import com.seathappens.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_kafka_events")
public class ProcessedKafkaEvent extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String consumerGroup;

    @Column(nullable = false)
    private LocalDateTime processedAt;

}
