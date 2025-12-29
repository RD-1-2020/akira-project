package org.azurecloud.solutions.akiraclient.service;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerEventService {

    private final SenderService senderService;
    private DockerClient dockerClient;
    private Closeable eventStream;

    @PostConstruct
    public void init() {
        log.info("Try to create DockerClient");
        DefaultDockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        var dockerClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        try {
            this.dockerClient = DockerClientImpl.getInstance(dockerConfig, dockerClient);

            log.info("DockerClient created");
            log.debug("Docker info: {}", this.dockerClient.infoCmd().exec());

            startEventStream();
        } catch (Exception exception) {
            log.error("Something went wrong on try listen docker events...", exception);
        }
    }

    private void startEventStream() {
        log.info("Starting Docker event stream...");
        eventStream = dockerClient.eventsCmd()
                .exec(new ResultCallback.Adapter<Event>() {
                    @Override
                    public void onNext(Event event) {
                        handleEvent(event);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("Docker event stream error", throwable);
                        // Implement retry logic if needed
                    }
                });
    }

    private void handleEvent(Event event) {
        var actor = event.getActor();
        String message = String.format("Docker Event: %s %s %s", event.getType(), event.getAction(),
                actor == null ? "unknown actor" : actor.getAttributes());
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getTime()), ZoneId.systemDefault());
        senderService.sendEvent(message, date);
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (eventStream != null) {
                eventStream.close();
            }
            if (dockerClient != null) {
                dockerClient.close();
            }
        } catch (IOException e) {
            log.error("Error closing docker client", e);
        }
    }
}
