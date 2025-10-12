package org.azurecloud.solutions.akira.service.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.Locale;
import java.util.Set;

import org.azurecloud.solutions.akira.model.entity.NotifierConfig;
import org.azurecloud.solutions.akira.repository.NotifierConfigRepository;
import org.azurecloud.solutions.akira.service.notifier.AkiraNotifier;
import org.azurecloud.solutions.akira.service.notifier.NotifierFactory;

/**
 * Service for the application's self-monitoring.
 * It checks for internet connectivity and sends notifications if the status changes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelfMonitoringService implements AkiraMonitoring {

    private final NotifierFactory notifierFactory;
    private final NotifierConfigRepository notifierConfigRepository;
    private final MessageSource messageSource;

    @Value("${monitoring.self.connectivity.hosts:8.8.8.8}")
    private Set<String> connectivityCheckHosts;

    @Value("${monitoring.self.connectivity.timeout.ms:5000}")
    private int connectivityCheckTimeoutMs;

    private boolean lastCheckStatus = true; // Assume connection is OK at startup

    // This needs a default notifier. Let's assume there's one with a known ID=1.
    // This is a temporary solution. A better approach would be to have a dedicated setting
    // in application.properties for the self-monitoring notifier ID.
    private static final long SELF_MONITORING_NOTIFIER_ID = 1L;

    /**
     * Performs an internet connectivity check. If the status changes from the previous check,
     * it sends a notification (e.g., "connection lost" or "connection restored").
     */
    @Override
    public void check() {
        // This is a temporary and inefficient way to get the notifier.
        // It should be cached.
        NotifierConfig config = notifierConfigRepository.findById(SELF_MONITORING_NOTIFIER_ID)
            .orElse(null);
        if(config == null) {
            log.warn("Self-monitoring notifier with ID {} not found. Cannot send notifications.", SELF_MONITORING_NOTIFIER_ID);
            return;
        }
        AkiraNotifier notifier = notifierFactory.createNotifier(config);

        log.debug("Performing self-monitoring connectivity check...");
        boolean isConnected = isInternetAvailable();
        
        if (isConnected && !lastCheckStatus) {
            log.info("Internet connection has been restored.");
            notifier.send(messageSource.getMessage("self.monitor.alert.restored", null, Locale.getDefault()));
        } else if (!isConnected && lastCheckStatus) {
            log.warn("Internet connection has been lost.");
            notifier.send(messageSource.getMessage("self.monitor.alert.lost", null, Locale.getDefault()));
        }
        
        lastCheckStatus = isConnected;
    }

    private boolean isInternetAvailable() {
        boolean isAvailable = false;
        for (String host : connectivityCheckHosts) {
            try {
                InetAddress address = InetAddress.getByName(host);
                isAvailable |= address.isReachable(connectivityCheckTimeoutMs);
            } catch (Exception e) {
                log.error("Connectivity check failed: {}", e.getMessage());
                continue;
            }
        }

        return isAvailable;        
    }
}
