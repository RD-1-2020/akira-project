package org.azurecloud.solutions.akira.service.monitor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azurecloud.solutions.akira.service.notifier.AkiraNotifier;
import org.azurecloud.solutions.akira.service.notifier.NotifierFactory;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.azurecloud.solutions.akira.model.entity.SyslogMessage;
import org.azurecloud.solutions.akira.model.entity.SyslogMonitorConfig;
import org.azurecloud.solutions.akira.repository.SyslogMessageRepository;
import org.azurecloud.solutions.akira.repository.SyslogMonitorConfigRepository;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Locale;

/**
 * Service for handling Syslog messages.
 * Provides functionality for saving and searching messages.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyslogMonitoringService implements AkiraMonitoring {

    private final SyslogMessageRepository syslogMessageRepository;
    private final SyslogMonitorConfigRepository configRepository;
    private final NotifierFactory notifierFactory;
    private final MessageSource messageSource;

    @Override
    public void check() {
        log.debug("Checking for stale Syslog sources...");
        List<SyslogMonitorConfig> configs = configRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (SyslogMonitorConfig config : configs) {
            if (config.getNotifierConfig() == null) {
                // Cannot send alert if no notifier is configured
                continue;
            }

            if (config.getLastMessageAt() == null) {
                // We could alert here if we expect messages right after config creation,
                // but for now, we'll wait for the first message.
                continue;
            }

            Duration timeSinceLastMessage = Duration.between(config.getLastMessageAt(), now);
            if (timeSinceLastMessage.compareTo(config.getNoMessageInterval()) > 0) {
                log.warn("Syslog source '{}' ({}) is stale. Last message was at {}.",
                        config.getName(), config.getSourceUrl(), config.getLastMessageAt());
                
                AkiraNotifier notifier = notifierFactory.createNotifier(config.getNotifierConfig());
                String message = messageSource.getMessage("syslog.monitor.alert.stale",
                        new Object[]{config.getName(), config.getSourceUrl(), config.getNoMessageInterval().toString()},
                        Locale.getDefault());


                // To avoid spamming, we could add logic here to only send the alert once,
                // e.g., by adding a boolean 'alertSent' flag to the config.
                // For now, it will alert on every check until a new message arrives.
            }
        }
    }
    
    /**
     * Processes an incoming Syslog message. It first validates the source IP
     * against the configured monitors and then saves the message.
     * @param sourceAddress The IP address of the sender.
     * @param rawMessage The raw message content.
     */
    public void processMessage(String sourceAddress, String rawMessage) {
        // Validate that the message is from a configured and monitored source
        Optional<SyslogMonitorConfig> configOpt = configRepository.findBySourceUrl(sourceAddress);
        if (configOpt.isEmpty()) {
            log.warn("Received syslog message from unconfigured source: {}", sourceAddress);
            return;
        }
        
        SyslogMonitorConfig config = configOpt.get();
        config.setLastMessageAt(LocalDateTime.now());
        configRepository.save(config);

        saveMessage(config, rawMessage);
    }
    
    /**
     * Saves a new Syslog message to the database, linking it to its source configuration.
     * @param sourceConfig The configuration of the source.
     * @param message The content of the syslog message.
     */
    private void saveMessage(SyslogMonitorConfig sourceConfig, String message) {
        log.info("Received syslog message from [{}]: {}", sourceConfig.getSourceUrl(), message);
        SyslogMessage syslogMessage = new SyslogMessage();
        syslogMessage.setSource(sourceConfig);
        syslogMessage.setMessage(message);
        syslogMessage.setReceivedAt(LocalDateTime.now());
        syslogMessageRepository.save(syslogMessage);
    }

    /**
     * Searches for Syslog messages.
     * @param term The text to search for in the message content.
     * @return A list of matching {@link SyslogMessage}s.
     */
    @Transactional(readOnly = true)
    public List<SyslogMessage> searchMessages(String term) {
        return syslogMessageRepository.findByMessageContainingIgnoreCase(term);
    }
}
