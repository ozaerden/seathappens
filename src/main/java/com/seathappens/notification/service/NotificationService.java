package com.seathappens.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import com.seathappens.notification.config.NotificationProperties;
import com.seathappens.notification.entity.Notification;
import com.seathappens.notification.repository.NotificationRepository;
import com.seathappens.order.entity.Order;
import com.seathappens.order.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Transactional
    public void createPaymentSucceededNotification(UUID eventId, String eventType, String payload) {
        if (notificationRepository.existsByEventId(eventId)) {
            return;
        }

        PaymentSucceededEvent event = deserializePaymentSucceededEvent(payload);
        Order order = orderRepository.findById(event.orderId())
                .orElse(null);

        String recipient = order == null
                ? notificationProperties.defaultRecipient()
                : order.getUser().getEmail();

        Notification notification = Notification.builder()
                .eventId(eventId)
                .eventType(eventType)
                .referenceId(event.orderId())
                .recipient(recipient)
                .subject("Your Seat Happens ticket is ready")
                .content("""
                        <h2>Your ticket purchase is confirmed</h2>
                        <p>Payment succeeded and your ticket has been issued.</p>
                        <ul>
                            <li><strong>Payment ID:</strong> %s</li>
                            <li><strong>Order ID:</strong> %s</li>
                            <li><strong>Amount:</strong> %s</li>
                        </ul>
                        <p>Please find your ticket QR code attachment below.</p>
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
