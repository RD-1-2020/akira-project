package org.azurecloud.solutions.akira.fallback;

import java.net.InetSocketAddress;
import java.net.Socket;

public final class HostReachability {

    private HostReachability() {

    }

    public static boolean canConnect(DockerHost host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host.hostname(), port), timeoutMs);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}