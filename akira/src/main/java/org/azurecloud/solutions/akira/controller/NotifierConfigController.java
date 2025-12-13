package org.azurecloud.solutions.akira.controller;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.controller.config.Endpoints;
import org.azurecloud.solutions.akira.model.entity.NotificationMessage;
import org.azurecloud.solutions.akira.model.entity.NotifierConfig;
import org.azurecloud.solutions.akira.service.config.NotificationMessageService;
import org.azurecloud.solutions.akira.service.config.NotifierConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(Endpoints.NOTIFIERS)
@RequiredArgsConstructor
public class NotifierConfigController {

    private final NotifierConfigService notifierConfigService;
    private final NotificationMessageService notificationMessageService;

    // == Notifier Config CRUD ==

    @GetMapping
    public List<NotifierConfig> getAllNotifierConfigs() {
        return notifierConfigService.getAll();
    }

    @GetMapping("/{id}")
    public NotifierConfig getNotifierById(@PathVariable @NonNull Long id) {
        return notifierConfigService.getById(id);
    }

    @PostMapping
    public NotifierConfig createNotifierConfig(@RequestBody @Valid @NonNull NotifierConfig config) {
        return notifierConfigService.create(config);
    }

    @PutMapping("/{id}")
    public NotifierConfig updateNotifier(@PathVariable @NonNull Long id, @RequestBody @Valid @NonNull NotifierConfig config) {
        return notifierConfigService.update(id, config);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteNotifierConfig(@PathVariable @NonNull Long id) {
        notifierConfigService.delete(id);
    }

    // == Notification Messages ==

    @GetMapping(Endpoints.MESSAGES)
    public List<NotificationMessage> getAllMessages() {
        return notificationMessageService.getAll();
    }

    @GetMapping(Endpoints.MESSAGES + "/{id}")
    public NotificationMessage getMessageById(@PathVariable @NonNull Long id) {
        return notificationMessageService.getById(id);
    }

    @PostMapping(Endpoints.MESSAGES + "/{id}/retry")
    public NotificationMessage retryMessage(@PathVariable @NonNull Long id) {
        return notificationMessageService.retry(id);
    }

    @DeleteMapping(Endpoints.MESSAGES + "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable @NonNull Long id) {
        notificationMessageService.delete(id);
    }
}
