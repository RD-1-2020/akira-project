package org.azurecloud.solutions.akira.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.azurecloud.solutions.akira.model.entity.Client;
import org.azurecloud.solutions.akira.model.entity.ClientMessage;
import org.azurecloud.solutions.akira.service.ClientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{hostname}/messages")
    public Page<ClientMessage> getClientMessages(
            @PathVariable String hostname,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20, sort = "receivedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return clientService.getClientMessages(hostname, from, to, pageable);
    }

    @GetMapping("/{hostname}/stats")
    public ResponseEntity<Map<Integer, Long>> getClientStats(@PathVariable String hostname) {
        return ResponseEntity.ok(clientService.getMessageCountByHour(hostname));
    }
}

