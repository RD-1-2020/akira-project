package org.azurecloud.solutions.akira.service.config;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.model.entity.HttpMonitorConfig;
import org.azurecloud.solutions.akira.exception.AkiraErrorCode;
import org.azurecloud.solutions.akira.exception.AkiraException;
import org.azurecloud.solutions.akira.repository.HttpMonitorConfigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HttpMonitorConfigService {

    private final HttpMonitorConfigRepository repository;

    public List<HttpMonitorConfig> getAll() {
        return repository.findAll();
    }

    public HttpMonitorConfig getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public HttpMonitorConfig create(HttpMonitorConfig config) {
        return repository.save(config);
    }

    public HttpMonitorConfig update(Long id, HttpMonitorConfig config) {
        HttpMonitorConfig existingConfig = getById(id);
        existingConfig.setName(config.getName());
        existingConfig.setUrl(config.getUrl());
        return repository.save(existingConfig);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
