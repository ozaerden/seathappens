package com.seathappens.notification.controller;

import com.seathappens.notification.dto.response.NotificationResponse;
import com.seathappens.notification.entity.NotificationStatus;
import com.seathappens.notification.service.NotificationAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification Admin", description = "Notification administration APIs")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationAdminController {

    private final NotificationAdminService notificationAdminService;

    @Operation(summary = "List notifications.")
    @GetMapping(version = "1")
    public List<NotificationResponse> getNotifications(@RequestParam(required = false) NotificationStatus status) {
        return notificationAdminService.getNotifications(status);
    }

    @Operation(summary = "Get notification by id.")
    @GetMapping(value = "/{id}", version = "1")
    public NotificationResponse getNotificationById(@PathVariable UUID id) {
        return notificationAdminService.getNotificationById(id);
    }

    @Operation(summary = "Retry failed notification.")
    @PostMapping(value = "/retry/{id}", version = "1")
    public NotificationResponse retryFailedNotification(@PathVariable UUID id) {
        return notificationAdminService.retryFailedNotification(id);
    }
}
