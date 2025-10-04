package org.azurecloud.solutions.akira.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.azurecloud.solutions.akira.service.monitor.HttpMonitoringService;
import org.azurecloud.solutions.akira.service.monitor.SelfMonitoringService;
import org.azurecloud.solutions.akira.service.monitor.SyslogMonitoringService;

/**
 * A scheduler that periodically runs all monitoring checks.
 */
@Component
@RequiredArgsConstructor
public class MonitoringScheduler {

    private final HttpMonitoringService httpMonitoringService;
    private final SelfMonitoringService selfMonitoringService;
    private final SyslogMonitoringService syslogMonitoringService;

    /**
     * Runs HTTP(S) monitoring checks at a fixed rate defined in the application properties.
     */
    @Scheduled(fixedRateString = "${monitoring.http.scheduler.rate.ms:60000}")
    public void runHttpChecks() {
        httpMonitoringService.check();
    }

    /**
     * Runs self-monitoring connectivity checks at a fixed rate defined in the application properties.
     */
    @Scheduled(fixedRateString = "${monitoring.self.scheduler.rate.ms:30000}")
    public void runSelfChecks() {
        selfMonitoringService.check();
    }

    /**
     * Runs checks for stale Syslog sources at a fixed rate defined in the application properties.
     */
    @Scheduled(fixedRateString = "${monitoring.syslog.scheduler.rate.ms:60000}")
    public void runSyslogChecks() {
        syslogMonitoringService.check();
    }
}
