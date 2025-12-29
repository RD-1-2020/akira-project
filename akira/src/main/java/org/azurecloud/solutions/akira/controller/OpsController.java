package org.azurecloud.solutions.akira.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.model.dto.ClientEventDto;
import org.azurecloud.solutions.akira.service.ClientService;

@RestController
@RequestMapping("/api/ops")
@RequiredArgsConstructor
public class OpsController {

    private final ClientService clientService;

    @Value("${akira.ops.token}")
    private String opsToken;

    @PostMapping("/event")
    public ResponseEntity<Void> receiveEvent(
            @RequestHeader("X-Ops-Token") String token,
            @RequestBody ClientEventDto event) {
        
        if (!opsToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        clientService.processEvent(event.getClientHostname(), event.getMessage(), event.getDate());
        return ResponseEntity.ok().build();
    }
}

