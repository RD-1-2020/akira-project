package org.azurecloud.solutions.akira.controller;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.controller.config.Endpoints;
import org.azurecloud.solutions.akira.model.entity.SyslogMessage;
import org.azurecloud.solutions.akira.model.entity.SyslogMonitorConfig;
import org.azurecloud.solutions.akira.service.config.SyslogMonitorConfigService;
import org.azurecloud.solutions.akira.service.monitor.SyslogMonitoringService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.MONITORS_SYSLOG)
@RequiredArgsConstructor
public class SyslogMonitorConfigController {

    private final SyslogMonitorConfigService syslogMonitorConfigService;
    private final SyslogMonitoringService syslogMonitoringService;

    @GetMapping
    public List<SyslogMonitorConfig> getAllSyslogMonitorConfigs() {
        return syslogMonitorConfigService.getAll();
    }

    @GetMapping("/{id}")
    public SyslogMonitorConfig getById(@PathVariable Long id) {
        return syslogMonitorConfigService.getById(id);
    }

    @PostMapping
    public SyslogMonitorConfig createSyslogMonitorConfig(@RequestBody SyslogMonitorConfig config) {
        return syslogMonitorConfigService.create(config);
    }

    @PutMapping("/{id}")
    public SyslogMonitorConfig update(@PathVariable Long id, @RequestBody SyslogMonitorConfig config) {
        return syslogMonitorConfigService.update(id, config);
    }

    @DeleteMapping("/{id}")
    public void deleteSyslogMonitorConfig(@PathVariable Long id) {
        syslogMonitorConfigService.delete(id);
    }

    /**
     * Searches for Syslog messages based on optional query parameters.
     * @param text Optional text to search for within the message body.
     * @return A list of matching {@link SyslogMessage} entities.
     */
    @GetMapping(Endpoints.MESSAGES_SUFFIX)
    public List<SyslogMessage> searchSyslogMessages(
            @RequestParam(required = false) String text) {
        return syslogMonitoringService.searchMessages(text);
    }
}
