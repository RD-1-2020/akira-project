package org.azurecloud.solutions.akira.service.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.azurecloud.solutions.akira.model.entity.HttpMonitorConfig;
import org.azurecloud.solutions.akira.service.notifier.AkiraNotifier;
import org.azurecloud.solutions.akira.service.notifier.NotifierFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.springframework.context.MessageSource;
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
        log.debug("Checking all HTTP monitors...");
        List<MonitorConfig> configs = configRepository.findAll();
        for (MonitorConfig config : configs) {
            if (config instanceof HttpMonitorConfig) {
                check((HttpMonitorConfig) config);
            }
        }
    }

    /**
     * Performs a check for a single HTTP(S) endpoint configuration.
     * Sends a notification if the endpoint is down or if an error occurs.
     * @param config The configuration of the endpoint to check.
     */
    public void check(HttpMonitorConfig config) {
        log.info("Checking URL: {}", config.getUrl());
        if (config.getNotifierConfig() == null) {
            return;
        }
        AkiraNotifier notifier = notifierFactory.createNotifier(config.getNotifierConfig());
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getUrl()))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != config.getExpectedStatus()) {
                log.warn("URL {} is DOWN (status code: {}, expected: {})", config.getUrl(), response.statusCode(), config.getExpectedStatus());
                String message = messageSource.getMessage("http.monitor.alert.down", new Object[]{config.getName(), config.getUrl(), response.statusCode()}, Locale.getDefault());
                notifier.send(message);
            } else {
                log.info("URL {} is OK (status code: {})", config.getUrl(), response.statusCode());
            }
        } catch (Exception e) {
            log.error("Failed to check URL {}: {}", config.getUrl(), e.getMessage());
            String message = messageSource.getMessage("http.monitor.alert.error", new Object[]{config.getName(), config.getUrl(), e.getMessage()}, Locale.getDefault());
            notifier.send(message);
        }
    }
}
