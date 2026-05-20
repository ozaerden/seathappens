package com.seathappens.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import com.seathappens.notification.config.NotificationProperties;
import com.seathappens.notification.entity.Notification;
import com.seathappens.notification.repository.NotificationRepository;
import com.seathappens.payment.event.PaymentSucceededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationProperties notificationProperties;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createPaymentSucceededNotification(UUID eventId, String eventType, String payload) {
        if (notificationRepository.existsByEventId(eventId)) {
            return;
        }

        PaymentSucceededEvent event = deserializePaymentSucceededEvent(payload);

        Notification notification = Notification.builder()
                .eventId(eventId)
                .eventType(eventType)
                .recipient(notificationProperties.defaultRecipient())
                .subject("Your ticket purchase is confirmed")
                .content("""
                        Payment succeeded.
                        Payment ID: %s
                        Order ID: %s
                        Amount: %s
                        """.formatted(
                        event.paymentId(),
                        event.orderId(),
                        event.amount()
                ))
                .build();

        notificationRepository.save(notification);
    }

    private PaymentSucceededEvent deserializePaymentSucceededEvent(String payload) {
        try {
            return objectMapper.readValue(payload, PaymentSucceededEvent.class);
        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(
                    ErrorCode.OUTBOX_SERIALIZATION_ERROR,
                    exception
            );
        }
    }
    
}
