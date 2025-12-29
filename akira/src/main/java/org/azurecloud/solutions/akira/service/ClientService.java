package org.azurecloud.solutions.akira.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azurecloud.solutions.akira.model.entity.Client;
import org.azurecloud.solutions.akira.model.entity.ClientMessage;
import org.azurecloud.solutions.akira.model.entity.ClientStatus;
import org.azurecloud.solutions.akira.repository.ClientMessageRepository;
import org.azurecloud.solutions.akira.repository.ClientRepository;
import org.azurecloud.solutions.akira.repository.NotifierConfigRepository;
import org.azurecloud.solutions.akira.service.notifier.AkiraNotifier;
import org.azurecloud.solutions.akira.service.notifier.NotifierFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMessageRepository clientMessageRepository;
    private final NotifierConfigRepository notifierConfigRepository;
    private final NotifierFactory notifierFactory;
    private final MessageSource messageSource;

    @Value("${akira.monitoring.client-timeout-seconds:300}")
    private long clientTimeoutSeconds;

    @Transactional
    public void processEvent(String hostname, String message, LocalDateTime clientTimestamp) {
        log.debug("Processing event from {}: {}", hostname, message);
        LocalDateTime now = LocalDateTime.now();

        Client client = clientRepository.findById(hostname)
                .orElseGet(() -> {
                    Client c = new Client();
                    c.setHostname(hostname);
                    c.setStatus(ClientStatus.OK);
                    return clientRepository.save(c);
                });

        // Save message
        ClientMessage clientMessage = new ClientMessage();
        clientMessage.setClient(client);
        clientMessage.setMessage(message);
        clientMessage.setClientTimestamp(clientTimestamp);
        clientMessage.setReceivedAt(now);
        clientMessageRepository.save(clientMessage);

        // Update client last message time
        client.setLastMessageAt(now);

        // Recovery check
        if (client.getStatus() == ClientStatus.FAILED) {
            client.setStatus(ClientStatus.OK);
            clientRepository.save(client);
            
            String msg = messageSource.getMessage("client.monitor.recovery", new Object[]{hostname}, Locale.getDefault());
            notifySystem(msg);
        } else {
            // Just save the updated timestamp
            clientRepository.save(client);
        }
    }

    @Transactional
    public void checkStaleClients() {
        log.debug("Checking for stale clients...");
        List<Client> clients = clientRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        Duration timeout = Duration.ofSeconds(clientTimeoutSeconds);

        for (Client client : clients) {
            if (client.getLastMessageAt() == null) {
                continue;
            }

            Duration timeSinceLast = Duration.between(client.getLastMessageAt(), now);
            if (timeSinceLast.compareTo(timeout) > 0) {
                if (client.getStatus() == ClientStatus.OK) {
                    client.setStatus(ClientStatus.FAILED);
                    clientRepository.save(client);
                    
                    // Fetch last message for alert
                    Page<ClientMessage> lastMsgPage = clientMessageRepository.findByClientHostname(
                            client.getHostname(), 
                            PageRequest.of(0, 1, Sort.by("receivedAt").descending())
                    );
                    String lastMsgContent = "N/A";
                    if (lastMsgPage.hasContent()) {
                         lastMsgContent = lastMsgPage.getContent().getFirst().getMessage();
                    }

                    String msg = messageSource.getMessage("client.monitor.alert.failed", 
                            new Object[]{client.getHostname(), timeSinceLast.toSeconds(), lastMsgContent}, 
                            Locale.getDefault());
                    notifySystem(msg);
                }
            }
        }
    }

    private void notifySystem(String text) {
        try {
            notifierConfigRepository.findById(1L).ifPresent(config -> {
                AkiraNotifier notifier = notifierFactory.createNotifier(config);
                notifier.send(text);
                log.info("System notification sent: {}", text);
            });
        } catch (Exception e) {
            log.error("Failed to send system notification", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ClientMessage> getClientMessages(String hostname, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if (from != null && to != null) {
            return clientMessageRepository.findByClientHostnameAndReceivedAtBetween(hostname, from, to, pageable);
        }
        return clientMessageRepository.findByClientHostname(hostname, pageable);
    }
    
    @Transactional(readOnly = true)
    public Map<Integer, Long> getMessageCountByHour(String hostname) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Object[]> results;
        if (hostname != null) {
            results = clientMessageRepository.countMessagesByHourAndClientSince(hostname, startOfDay);
        } else {
             results = clientMessageRepository.countMessagesByHourSince(startOfDay);
        }

        Map<Integer, Long> map = new TreeMap<>();
        // Initialize 0-23
        for(int i=0; i<24; i++) map.put(i, 0L);

        for (Object[] row : results) {
            if (row[0] instanceof Number num) {
                 int hour = num.intValue();
                 Long count = ((Number) row[1]).longValue();
                 map.put(hour, count);
            }
        }
        return map;
    }
}
