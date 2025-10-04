package org.azurecloud.solutions.akira.controller;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.controller.config.Endpoints;
import org.azurecloud.solutions.akira.model.entity.HttpMonitorConfig;
import org.azurecloud.solutions.akira.service.config.HttpMonitorConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Endpoints.MONITORS_HTTP)
@RequiredArgsConstructor
public class HttpMonitorConfigController {

    private final HttpMonitorConfigService httpMonitorConfigService;

    @GetMapping
    public List<HttpMonitorConfig> getAllHttpMonitorConfigs() {
        return httpMonitorConfigService.getAll();
    }

    @PostMapping
    public HttpMonitorConfig createHttpMonitorConfig(@RequestBody HttpMonitorConfig config) {
        return httpMonitorConfigService.create(config);
    }

    @DeleteMapping("/{id}")
    public void deleteHttpMonitorConfig(@PathVariable Long id) {
        httpMonitorConfigService.delete(id);
    }
}
