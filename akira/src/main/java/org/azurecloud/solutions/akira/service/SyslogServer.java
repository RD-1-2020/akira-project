package org.azurecloud.solutions.akira.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.azurecloud.solutions.akira.service.monitor.SyslogMonitoringService;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A background service that runs a UDP server to listen for Syslog messages on port 514.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyslogServer {

    private final SyslogMonitoringService syslogMonitoringService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private DatagramSocket socket;
    private volatile boolean running;

    @PostConstruct
    public void start() {
        executor.submit(() -> {
            try {
                socket = new DatagramSocket(514);
                running = true;
                log.info("Syslog UDP server started on port 514");

                while (running) {
                    try {
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String sourceAddress = packet.getAddress().getHostAddress();
                        String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                        
                        // Delegate to the monitoring service, which contains the validation logic
                        syslogMonitoringService.processMessage(sourceAddress, message);
                        
                    } catch (IOException e) {
                        if (running) {
                            log.error("Error receiving syslog packet", e);
                        }
                    }
                }
            } catch (SocketException e) {
                log.error("Could not start syslog UDP server on port 514. Port may be in use or requires privileged access.", e);
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                log.info("Syslog UDP server stopped");
            }
        });
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();
        }
        executor.shutdownNow();
    }
}
