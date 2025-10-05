package org.azurecloud.solutions.akira.controller;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.controller.config.Endpoints;
import org.azurecloud.solutions.akira.model.entity.MonitorConfig;
import org.azurecloud.solutions.akira.service.config.MonitorConfigService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Endpoints.MONITORS)
@RequiredArgsConstructor
public class MonitorConfigController {

    private final MonitorConfigService monitorConfigService;

    @GetMapping
    public List<MonitorConfig> getAll() {
        return monitorConfigService.getAll();
    }

    @PostMapping
    public MonitorConfig create(@RequestBody @Valid MonitorConfig monitorConfig) {
        return monitorConfigService.create(monitorConfig);
    }

    @PutMapping("/{id}")
    public MonitorConfig update(@PathVariable Long id, @RequestBody @Valid MonitorConfig monitorConfig) {
        return monitorConfigService.update(id, monitorConfig);
    }

    @DeleteMapping("/{id}")
    public void deleteMonitor(@PathVariable Long id) {
        monitorConfigService.delete(id);
    }
}
