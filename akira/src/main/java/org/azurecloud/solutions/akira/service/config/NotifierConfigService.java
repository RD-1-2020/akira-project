package org.azurecloud.solutions.akira.service.config;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.model.entity.NotifierConfig;
import org.azurecloud.solutions.akira.exception.AkiraErrorCode;
import org.azurecloud.solutions.akira.exception.AkiraException;
import org.azurecloud.solutions.akira.repository.NotifierConfigRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifierConfigService {

    private final NotifierConfigRepository repository;

    public List<NotifierConfig> getAll() {
        return repository.findAll();
    }

    public NotifierConfig getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public NotifierConfig create(NotifierConfig config) {
        return repository.save(config);
    }

    public NotifierConfig update(Long id, NotifierConfig config) {
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
