package com.seathappens.notification.scheduler;

import com.seathappens.notification.service.NotificationEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEmailScheduler {

    private final NotificationEmailService notificationEmailService;

    @Scheduled(fixedRateString = "${seathappens.notification.email-scheduler-fixed-rate-ms}")
    public void sendCreatedNotifications() {
        int sentOrFailedCount = notificationEmailService.sendCreatedNotifications();

        if (sentOrFailedCount > 0) {
            log.info("Processed notification emails. count={}", sentOrFailedCount);
        }
    }
}
