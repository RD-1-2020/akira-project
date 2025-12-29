package org.azurecloud.solutions.akiraclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderService {

    private final RestTemplate restTemplate;

    @Value("${akira.server.url}")
    private String serverUrl;

    @Value("${akira.server.token}")
    private String serverToken;

    @Value("${akira.client.hostname}")
    private String hostname;

    public void sendEvent(String message, LocalDateTime date) {
        try {
            String url = serverUrl + "/api/ops/event";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Ops-Token", serverToken);

            Map<String, Object> body = new HashMap<>();
            body.put("clientHostname", hostname);
            body.put("message", message);
            body.put("date", date.toString());

            var request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, request, Void.class);
            log.debug("Sent event: {}", message);
        } catch (Exception e) {
            log.error("Failed to send event: {}", e.getMessage());
        }
    }
}

