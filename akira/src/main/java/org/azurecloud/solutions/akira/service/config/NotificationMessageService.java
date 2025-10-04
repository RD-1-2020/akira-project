package org.azurecloud.solutions.akira.service.config;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.model.entity.NotificationMessage;
import org.azurecloud.solutions.akira.exception.AkiraErrorCode;
import org.azurecloud.solutions.akira.exception.AkiraException;
import org.azurecloud.solutions.akira.repository.NotificationMessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationMessageService {

    private final NotificationMessageRepository repository;

    public List<NotificationMessage> getAll() {
        return repository.findAll();
    }

    public NotificationMessage getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public NotificationMessage retry(Long id) {
        NotificationMessage message = getById(id);
        if (message.getStatus() != NotificationMessage.Status.FAILED) {
            throw new AkiraException(AkiraErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST,
                    "Only messages with FAILED status can be retried.");
        }
        message.setStatus(NotificationMessage.Status.CREATED);
        message.setUpdatedAt(LocalDateTime.now());
        return repository.save(message);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new AkiraException(AkiraErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
