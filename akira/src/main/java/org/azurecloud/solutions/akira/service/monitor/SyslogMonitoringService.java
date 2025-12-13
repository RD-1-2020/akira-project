package org.azurecloud.solutions.akira.service.monitor;

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
import org.azurecloud.solutions.akira.model.entity.MonitorStatus;
import org.azurecloud.solutions.akira.repository.SyslogMessageRepository;
import org.azurecloud.solutions.akira.repository.MonitorConfigRepository;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
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
                handleSyslogMonitorStatus(syslogConfig, now);
            }
        }
    }

    /**
     * Handles syslog monitor status changes and notifications based on the business
     * logic:
     * - If monitor is ACTIVE and becomes stale: set to FAILED and send notification
     * - If monitor is FAILED and becomes stale: log only, no notification
     */
    private void handleSyslogMonitorStatus(SyslogMonitorConfig syslogConfig, LocalDateTime now) {
        if (syslogConfig.getNotifierConfig() == null || syslogConfig.getLastMessageAt() == null) {
            return;
        }

        Duration timeSinceLastMessage = Duration.between(syslogConfig.getLastMessageAt(), now);
        boolean isStale = timeSinceLastMessage.compareTo(syslogConfig.getNoMessageInterval()) > 0;

        if (!isStale) {
            log.trace("Syslog monitor '{}' ({}) is not stale. Last message was at {}.",
                    syslogConfig.getName(), syslogConfig.getSourceIp(), syslogConfig.getLastMessageAt());
            return;
        }

        if (syslogConfig.getStatus() == MonitorStatus.FAILED) {
            log.trace("Syslog monitor '{}' is already FAILED - no notification sent", syslogConfig.getName());
            return;
        }

        // Monitor just became stale - send notification and update status
        syslogConfig.setStatus(MonitorStatus.FAILED);
        configRepository.save(syslogConfig);

        log.debug("Syslog monitor '{}' ({}) is stale. Last message was at {}.",
                syslogConfig.getName(), syslogConfig.getSourceIp(), syslogConfig.getLastMessageAt());

        AkiraNotifier notifier = notifierFactory.createNotifier(syslogConfig.getNotifierConfig());
        String message = messageSource.getMessage("syslog.monitor.alert.stale",
                new Object[]{syslogConfig.getName(), syslogConfig.getSourceIp(),
                        syslogConfig.getNoMessageInterval().toString()},
                Locale.getDefault());
        notifier.send(message);
        log.info("Syslog monitor '{}' failed - notification sent", syslogConfig.getName());
    }

    @Transactional
    public void processMessage(String sourceAddress, String rawMessage) {
        var syslogMonitorOpt = configRepository.findBySourceIp(sourceAddress);
        if (syslogMonitorOpt.isEmpty()) {
            log.debug("Received syslog message from unconfigured source: {}", sourceAddress);
            return;
        }

        SyslogMonitorConfig syslogMonitor = syslogMonitorOpt.get();

        // Check if this is a recovery (monitor was FAILED and now receiving messages)
        boolean wasFailed = syslogMonitor.getStatus() == MonitorStatus.FAILED;

        syslogMonitor.setLastMessageAt(LocalDateTime.now());
        saveMessage(syslogMonitor, rawMessage);

        if (wasFailed) {
            // Monitor recovered - send recovery notification
            syslogMonitor.setStatus(MonitorStatus.ACTIVE);

            String monitorName = syslogMonitor.getName();
            if (syslogMonitor.getNotifierConfig() == null) {
                log.warn("Syslog monitor '{}' has no notifier config", monitorName);
                return;
            }

            AkiraNotifier notifier = notifierFactory.createNotifier(syslogMonitor.getNotifierConfig());
            String message = messageSource.getMessage("syslog.monitor.recovery",
                    new Object[]{monitorName, syslogMonitor.getSourceIp()}, Locale.getDefault());
            log.debug("Syslog recovery message: '{}' for monitor: '{}'", message, monitorName);
            notifier.send(message);
            log.info("Syslog monitor '{}' recovered - notification sent", monitorName);
        }

        configRepository.save(syslogMonitor);
    }

    private void saveMessage(SyslogMonitorConfig sourceConfig, String message) {
        log.trace("Received syslog message from [{}]: {}", sourceConfig.getSourceIp(), message);
        SyslogMessage syslogMessage = new SyslogMessage();
        syslogMessage.setSourceId(sourceConfig.getId());
        syslogMessage.setMessage(message);
        syslogMessage.setReceivedAt(LocalDateTime.now());
        syslogMessageRepository.save(syslogMessage);
    }
}
