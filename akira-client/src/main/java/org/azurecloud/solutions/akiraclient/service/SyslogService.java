package org.azurecloud.solutions.akiraclient.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyslogService {

    private final SenderService senderService;

    private static final String SYSLOG_PATH = "/var/log/syslog";

    private long lastPosition = 0;
    private boolean initialized = false;

    @Scheduled(fixedDelay = 5000)
    public void readSyslog() {
        File file = new File(SYSLOG_PATH);
        if (!file.exists() || !file.canRead()) {
            return;
        }

        if (!initialized) {
            lastPosition = file.length();
            initialized = true;
            return;
        }

        if (file.length() < lastPosition) {
            // File was rotated
            lastPosition = 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.skip(lastPosition);
            String line;
            while ((line = reader.readLine()) != null) {
                lastPosition += line.length() + 1; // +1 for newline (approx)
                senderService.sendEvent("Syslog: " + line, LocalDateTime.now());
            }
        } catch (IOException e) {
            log.error("Error reading syslog", e);
        }
    }
}
