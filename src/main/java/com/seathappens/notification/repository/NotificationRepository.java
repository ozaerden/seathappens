package com.seathappens.notification.repository;

import com.seathappens.notification.entity.Notification;
import com.seathappens.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    boolean existsByEventId(UUID eventId);

    List<Notification> findTop20ByStatusAndNextRetryAtIsNullOrStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
            NotificationStatus statusWithoutRetry,
            NotificationStatus statusReadyForRetry,
            LocalDateTime retryBefore
    );

}
