package com.seathappens.notification.service;

import com.seathappens.notification.config.NotificationProperties;
import com.seathappens.notification.entity.Notification;
import com.seathappens.notification.entity.NotificationStatus;
import com.seathappens.notification.repository.NotificationRepository;
import com.seathappens.ticket.entity.Ticket;
import com.seathappens.ticket.repository.TicketRepository;
import com.seathappens.ticket.service.TicketQrCodeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEmailService {

    private final NotificationRepository notificationRepository;
    private final TicketRepository ticketRepository;
    private final TicketQrCodeService ticketQrCodeService;
    private final JavaMailSender javaMailSender;
    private final NotificationProperties notificationProperties;

    @Transactional
    public int sendCreatedNotifications() {
        if (!notificationProperties.emailEnabled()) {
            return 0;
        }

        List<Notification> notifications = notificationRepository
                .findTop20ByStatusAndNextRetryAtIsNullOrStatusAndNextRetryAtBeforeOrderByCreatedAtAsc(
                        NotificationStatus.CREATED,
                        NotificationStatus.CREATED,
                        LocalDateTime.now()
                );

        notifications.forEach(this::sendNotification);

        return notifications.size();
    }

    private void sendNotification(Notification notification) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            if (notificationProperties.fromEmail() != null && !notificationProperties.fromEmail().isBlank()) {
                helper.setFrom(notificationProperties.fromEmail());
            }
            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getContent(), true);

            attachTicketQrCodes(notification, helper);

            javaMailSender.send(message);

            notification.setStatus(NotificationStatus.SENT);
            notification.setLastError(null);
            notification.setNextRetryAt(null);

            log.info(
                    "Notification email sent. notificationId={}, eventType={}, recipient={}",
                    notification.getId(),
                    notification.getEventType(),
                    notification.getRecipient()
            );
        } catch (Exception exception) {
            int nextRetryCount = notification.getRetryCount() + 1;

            notification.setRetryCount(nextRetryCount);
            notification.setLastError(exception.getMessage());

            if (nextRetryCount >= notificationProperties.emailMaxRetryCount()) {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setNextRetryAt(null);
            } else {
                notification.setStatus(NotificationStatus.CREATED);
                notification.setNextRetryAt(
                        LocalDateTime.now().plusNanos(notificationProperties.emailRetryDelayMs() * 1_000_000)
                );
            }

            log.warn(
                    "Notification email failed. notificationId={}, eventType={}, recipient={}, retryCount={}",
                    notification.getId(),
                    notification.getEventType(),
                    notification.getRecipient(),
                    nextRetryCount,
                    exception
            );
        }
    }

    private void attachTicketQrCodes(Notification notification, MimeMessageHelper helper) throws MessagingException {
        if (notification.getReferenceId() == null) {
            return;
        }

        List<Ticket> tickets = ticketRepository.findByOrderId(notification.getReferenceId());

        for (Ticket ticket : tickets) {
            byte[] qrCode = ticketQrCodeService.generate(ticket);
            String attachmentName = "ticket-%s-qr.png".formatted(ticket.getTicketCode());

            helper.addAttachment(
                    attachmentName,
                    new ByteArrayResource(qrCode)
            );
        }
    }
}
