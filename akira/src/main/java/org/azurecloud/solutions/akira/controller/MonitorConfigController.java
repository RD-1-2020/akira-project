package org.azurecloud.solutions.akira.controller;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.controller.config.Endpoints;
import org.azurecloud.solutions.akira.model.entity.MonitorConfig;
import org.azurecloud.solutions.akira.service.config.MonitorConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.MONITORS)
@RequiredArgsConstructor
public class MonitorConfigController {

    private final MonitorConfigService monitorConfigService;

    @GetMapping
    public List<MonitorConfig> getAllMonitors() {
        return monitorConfigService.getAll();
    }

    @PostMapping
    public MonitorConfig createMonitor(@RequestBody MonitorConfig config) {
        return monitorConfigService.create(config);
    }

    @PutMapping("/{id}")
    public MonitorConfig updateMonitor(@PathVariable Long id, @RequestBody MonitorConfig config) {
        return monitorConfigService.update(id, config);
    }

    @DeleteMapping("/{id}")
    public void deleteMonitor(@PathVariable Long id) {
        monitorConfigService.delete(id);
    }
}
