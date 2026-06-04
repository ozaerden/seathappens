package com.seathappens.notification.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.notification.dto.response.NotificationResponse;
import com.seathappens.notification.entity.Notification;
import com.seathappens.notification.entity.NotificationStatus;
import com.seathappens.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationAdminService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(NotificationStatus status) {
        List<Notification> notifications = status == null
                ? notificationRepository.findAll()
                : notificationRepository.findByStatus(status);

        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID id) {
        return notificationRepository.findById(id)
                .map(NotificationResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Transactional
    public NotificationResponse retryFailedNotification(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!NotificationStatus.FAILED.equals(notification.getStatus())) {
            throw new BusinessException(ErrorCode.NOTIFICATION_NOT_FAILED);
        }

        notification.setStatus(NotificationStatus.CREATED);
        notification.setRetryCount(0);
        notification.setLastError(null);
        notification.setNextRetryAt(null);

        return NotificationResponse.from(notification);
    }
}
