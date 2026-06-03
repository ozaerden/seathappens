package com.seathappens.notification.entity;

import com.seathappens.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false, length = 100)
    private String eventType;

    private UUID referenceId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationStatus status = NotificationStatus.CREATED;

    @Builder.Default
    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    private LocalDateTime nextRetryAt;

}
