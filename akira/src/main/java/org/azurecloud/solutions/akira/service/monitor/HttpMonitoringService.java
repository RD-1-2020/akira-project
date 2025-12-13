package org.azurecloud.solutions.akira.service.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;

import org.azurecloud.solutions.akira.model.entity.MonitorStatus;
import org.azurecloud.solutions.akira.model.entity.HttpMonitorConfig;
import org.azurecloud.solutions.akira.service.notifier.AkiraNotifier;
import org.azurecloud.solutions.akira.service.notifier.NotifierFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

import org.azurecloud.solutions.akira.repository.MonitorConfigRepository;
import org.azurecloud.solutions.akira.model.entity.MonitorConfig;

/**
 * Service responsible for monitoring HTTP(S) endpoints.
 * It fetches all configurations and checks each one.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HttpMonitoringService implements AkiraMonitoring {

    private final MonitorConfigRepository configRepository;
    private final NotifierFactory notifierFactory;
    private final HttpClient httpClient;
    private final MessageSource messageSource;

    /**
     * Checks all configured HTTP(S) endpoints.
     * This method is typically called by a scheduler.
     */
    @Override
    public void check() {
        List<MonitorConfig> configs = configRepository.findAll();
        log.debug("Found {} HTTP monitors to check", configs.size());
        for (MonitorConfig config : configs) {
            if (config instanceof HttpMonitorConfig) {
                check((HttpMonitorConfig) config);
            }
        }
    }

    /**
     * Performs a check for a single HTTP(S) endpoint configuration.
     * Implements smart notification logic based on monitor status.
     *
     * @param config The configuration of the endpoint to check.
     */
    @Transactional
    public void check(HttpMonitorConfig config) {
        boolean checkSuccessful = false;
        String errorMessage = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getUrl()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == config.getExpectedStatus()) {
                checkSuccessful = true;
                log.debug("[Monitor {}] URL {} is OK (status code: {})", config.getName(), config.getUrl(), response.statusCode());
            } else {
                errorMessage = String.format("Status code: %d, expected: %d", response.statusCode(), config.getExpectedStatus());
                log.info("[Monitor {}] URL {} is DOWN - {}", config.getName(), config.getUrl(), errorMessage);
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            log.error("[Monitor {}] Failed to check URL {}: {}", config.getName(), config.getUrl(), errorMessage);
        }

        handleMonitorStatusChange(config, checkSuccessful, errorMessage);
    }

    /**
     * Handles monitor status changes and notifications based on the business logic:
     * - If monitor is FAILED and check fails: log only, no notification
     * - If monitor is ACTIVE and check fails: set to FAILED and send notification
     * - If monitor is FAILED and check succeeds: set to ACTIVE and send recovery notification
     */
    private void handleMonitorStatusChange(HttpMonitorConfig config, boolean checkSuccessful, String errorMessage) {
        MonitorStatus currentStatus = config.getStatus();

        // Check succeeded
        String monitorName = config.getName();
        if (checkSuccessful && currentStatus == MonitorStatus.FAILED) {
            // Monitor recovered - send recovery notification
            log.debug("Monitor '{}' recovered - sending recovery notification", monitorName);
            config.setStatus(MonitorStatus.ACTIVE);
            configRepository.save(config);

            if (config.getNotifierConfig() == null) {
                log.warn("Monitor {} has no notifier config", monitorName);
                return;
            }

            AkiraNotifier notifier = notifierFactory.createNotifier(config.getNotifierConfig());
            String message = messageSource.getMessage("http.monitor.recovery",
                    new Object[]{monitorName, config.getUrl()}, Locale.getDefault());
            notifier.send(message);
            log.debug("Monitor '{}' recovered - notification sent", monitorName);

            return;
        }

        // Monitor failed
        if (currentStatus == MonitorStatus.ACTIVE) {
            // Monitor failed - set status and send notification
            log.debug("Monitor '{}' failed - setting status to FAILED", monitorName);
            config.setStatus(MonitorStatus.FAILED);
            configRepository.save(config);

            if (config.getNotifierConfig() == null) {
                log.warn("Monitor {} has no notifier config", monitorName);
                return;
            }

            AkiraNotifier notifier = notifierFactory.createNotifier(config.getNotifierConfig());
            String message = messageSource.getMessage("http.monitor.alert.down",
                    new Object[]{monitorName, config.getUrl(), errorMessage}, Locale.getDefault());
            notifier.send(message);
            log.debug("Monitor '{}' failed - notification sent", monitorName);

        } else {
            // Monitor was already FAILED - just log, no notification
            log.trace("Monitor '{}' still failed - no notification sent", monitorName);
        }
    }
}
