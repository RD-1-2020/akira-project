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
import org.azurecloud.solutions.akira.model.entity.MonitorConfig;
import org.azurecloud.solutions.akira.repository.SyslogMessageRepository;
import org.azurecloud.solutions.akira.repository.MonitorConfigRepository;

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
    private final MonitorConfigRepository configRepository;
    private final NotifierFactory notifierFactory;
    private final MessageSource messageSource;

    @Override
    public void check() {
        log.debug("Checking for stale Syslog sources...");
        List<MonitorConfig> configs = configRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (MonitorConfig config : configs) {
            if (config instanceof SyslogMonitorConfig syslogConfig) {
                if (syslogConfig.getNotifierConfig() == null || syslogConfig.getLastMessageAt() == null) {
                    continue;
                }

                Duration timeSinceLastMessage = Duration.between(syslogConfig.getLastMessageAt(), now);
                if (timeSinceLastMessage.compareTo(syslogConfig.getNoMessageInterval()) > 0) {
                    log.warn("Syslog source '{}' ({}) is stale. Last message was at {}.",
                            syslogConfig.getName(), syslogConfig.getSourceUrl(), syslogConfig.getLastMessageAt());
                    
                    AkiraNotifier notifier = notifierFactory.createNotifier(syslogConfig.getNotifierConfig());
                    String message = messageSource.getMessage("syslog.monitor.alert.stale",
                            new Object[]{syslogConfig.getName(), syslogConfig.getSourceUrl(), syslogConfig.getNoMessageInterval().toString()},
                            Locale.getDefault());
                    notifier.send(message);
                }
            }
        }
    }

    public void processMessage(String sourceAddress, String rawMessage) {
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

    private void saveMessage(SyslogMonitorConfig sourceConfig, String message) {
        log.info("Received syslog message from [{}]: {}", sourceConfig.getSourceUrl(), message);
        SyslogMessage syslogMessage = new SyslogMessage();
        syslogMessage.setSourceId(sourceConfig.getId());
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
    public List<SyslogMessage> searchMessages(Long sourceId, String term) {
        return syslogMessageRepository.findByMessageContainingIgnoreCase(term);
    }
}
