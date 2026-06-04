package com.seathappens.notification.dto.response;

import com.seathappens.notification.entity.Notification;
import com.seathappens.notification.entity.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID eventId,
        String eventType,
        UUID referenceId,
        String recipient,
        String subject,
        String content,
        NotificationStatus status,
        Integer retryCount,
        String lastError,
        LocalDateTime nextRetryAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getEventId(),
                notification.getEventType(),
                notification.getReferenceId(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getContent(),
                notification.getStatus(),
                notification.getRetryCount(),
                notification.getLastError(),
                notification.getNextRetryAt(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
