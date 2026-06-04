package com.seathappens.outbox.entity;

import com.seathappens.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_events")
public class OutboxEvent extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(length = 100)
    private String correlationId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OutboxEventStatus status = OutboxEventStatus.PENDING;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    private LocalDateTime nextRetryAt;

}
