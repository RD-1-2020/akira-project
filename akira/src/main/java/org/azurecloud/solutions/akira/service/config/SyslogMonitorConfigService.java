package org.azurecloud.solutions.akira.service.config;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.model.entity.SyslogMonitorConfig;
import org.azurecloud.solutions.akira.exception.AkiraErrorCode;
import org.azurecloud.solutions.akira.exception.AkiraException;
import org.azurecloud.solutions.akira.repository.SyslogMonitorConfigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SyslogMonitorConfigService {

    private final SyslogMonitorConfigRepository repository;

    public List<SyslogMonitorConfig> getAll() {
        return repository.findAll();
    }

    public SyslogMonitorConfig getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public SyslogMonitorConfig create(SyslogMonitorConfig config) {
        return repository.save(config);
    }

    public SyslogMonitorConfig update(Long id, SyslogMonitorConfig config) {
        SyslogMonitorConfig existingConfig = getById(id);
        existingConfig.setName(config.getName());
        existingConfig.setSourceUrl(config.getSourceUrl());
        return repository.save(existingConfig);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
