package org.azurecloud.solutions.akira.service.notifier;

import lombok.extern.slf4j.Slf4j;
import org.azurecloud.solutions.akira.model.entity.NotificationMessage;
import org.azurecloud.solutions.akira.model.entity.TelegramNotifierConfig;
import org.azurecloud.solutions.akira.repository.NotificationMessageRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

/**
 * An implementation of AkiraNotifier that sends messages via Telegram.
 * This is a stateful object, created by the factory for a specific configuration.
 */
@Slf4j
public class TelegramNotifier extends TelegramLongPollingBot implements AkiraNotifier {

    private final TelegramNotifierConfig config;
    private final NotificationMessageRepository notificationMessageRepository;

    public TelegramNotifier(TelegramNotifierConfig config, NotificationMessageRepository repo) {
        super(config.getBotToken());
        this.config = config;
        this.notificationMessageRepository = repo;
    }

    @Override
    public void send(String message) {
        // This still uses the guaranteed delivery mechanism
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessage(message);
        notificationMessage.setStatus(NotificationMessage.Status.CREATED);
        notificationMessage.setSenderId("telegram:" + config.getId()); // Identify by config ID
        notificationMessage.setCreatedAt(LocalDateTime.now());
        notificationMessage.setUpdatedAt(LocalDateTime.now());
        notificationMessageRepository.save(notificationMessage);
    }

    /**
     * This is the method that actually sends the message.
     * It's called by the scheduler.
     */
    public void executeSend(NotificationMessage message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(config.getChatId(), message.getMessage());
        execute(sendMessage);
    }

    @Override
    public String getBotUsername() {
        // Not strictly needed for sending, but required by the abstract class.
        return "AkiraMonitorBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We do not process incoming messages.
    }
}
