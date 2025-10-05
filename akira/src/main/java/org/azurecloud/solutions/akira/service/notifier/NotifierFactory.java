package org.azurecloud.solutions.akira.service.notifier;

import org.azurecloud.solutions.akira.model.entity.NotifierConfig;
import org.azurecloud.solutions.akira.model.entity.TelegramNotifierConfig;
import org.azurecloud.solutions.akira.repository.NotificationMessageRepository;
import org.springframework.stereotype.Component;

@Component
public class NotifierFactory {

    private final NotificationMessageRepository notificationMessageRepository;

    public NotifierFactory(NotificationMessageRepository notificationMessageRepository) {
        this.notificationMessageRepository = notificationMessageRepository;
    }

    public AkiraNotifier createNotifier(NotifierConfig config) {
        if (config instanceof TelegramNotifierConfig tnc) {
            // In a real-world scenario, you might want to cache these instances
            // instead of creating a new one every time.
            return new TelegramNotifier(tnc, notificationMessageRepository);
        }
        throw new IllegalArgumentException("Unknown notifier type: " + config.getClass().getSimpleName());
    }
}
