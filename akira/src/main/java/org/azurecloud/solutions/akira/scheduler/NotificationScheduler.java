package org.azurecloud.solutions.akira.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.azurecloud.solutions.akira.model.entity.TelegramNotifierConfig;
import org.azurecloud.solutions.akira.service.notifier.NotifierFactory;
import org.azurecloud.solutions.akira.service.notifier.TelegramNotifier;
import org.azurecloud.solutions.akira.model.entity.NotificationMessage;
import org.azurecloud.solutions.akira.repository.NotificationMessageRepository;
import org.azurecloud.solutions.akira.model.entity.NotifierConfig;
import org.azurecloud.solutions.akira.repository.NotifierConfigRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A scheduled task responsible for the guaranteed delivery of notifications.
 * It periodically checks for pending messages in the database and attempts to send them via Telegram.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationMessageRepository notificationMessageRepository;
    private final NotifierConfigRepository notifierConfigRepository;
    private final NotifierFactory notifierFactory;

    /**
     * Periodically fetches and sends notifications with the status {@link NotificationMessage.Status#CREATED}.
     * Updates the message status to {@link NotificationMessage.Status#DELIVERED} on success or
     * {@link NotificationMessage.Status#FAILED} on failure.
     */
    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void sendPendingNotifications() {
        log.debug("Checking for pending notifications...");
        List<NotificationMessage> pendingMessages = notificationMessageRepository.findAllByStatus(NotificationMessage.Status.CREATED);

        for (NotificationMessage message : pendingMessages) {
            log.info("Sending notification id: {}", message.getId());
            try {
                // Extract notifier config ID from senderId
                Long configId = Long.parseLong(message.getSenderId().split(":")[1]);
                NotifierConfig config = notifierConfigRepository.findById(configId)
                        .orElseThrow(() -> new RuntimeException("Notifier config not found for message: " + message.getId()));

                // This is a simplification. A better approach would be to manage bot instances
                // within the factory or another service to avoid re-creating them constantly.
                if (config instanceof TelegramNotifierConfig) {
                    TelegramNotifier notifier = (TelegramNotifier) notifierFactory.createNotifier(config);
                    notifier.executeSend(message);
                    message.setStatus(NotificationMessage.Status.DELIVERED);
                    log.info("Notification id: {} sent successfully", message.getId());
                } else {
                    throw new RuntimeException("Unsupported notifier type for message: " + message.getId());
                }

            } catch (Exception e) {
                log.error("Failed to send notification id: {}. Error: {}", message.getId(), e.getMessage());
                message.setStatus(NotificationMessage.Status.FAILED);
            }
            message.setUpdatedAt(LocalDateTime.now());
            notificationMessageRepository.save(message);
        }
    }
}
