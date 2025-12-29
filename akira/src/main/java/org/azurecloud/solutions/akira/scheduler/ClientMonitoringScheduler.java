package org.azurecloud.solutions.akira.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.service.ClientService;

@Component
@RequiredArgsConstructor
public class ClientMonitoringScheduler {

    private final ClientService clientService;

    @Scheduled(fixedRateString = "${akira.monitoring.scheduler.rate.ms:60000}")
    public void runChecks() {
        clientService.checkStaleClients();
    }
}

