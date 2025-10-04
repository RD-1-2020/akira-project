package org.azurecloud.solutions.akira.service.config;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.exception.AkiraErrorCode;
import org.azurecloud.solutions.akira.exception.AkiraException;
import org.azurecloud.solutions.akira.model.entity.MonitorConfig;
import org.azurecloud.solutions.akira.repository.MonitorConfigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitorConfigService {

    private final MonitorConfigRepository repository;

    public List<MonitorConfig> getAll() {
        return repository.findAll();
    }

    public MonitorConfig getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public MonitorConfig create(MonitorConfig config) {
        return repository.save(config);
    }

    public MonitorConfig update(Long id, MonitorConfig config) {
        if (!repository.existsById(id)) {
            throw new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        config.setId(id);
        return repository.save(config);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
