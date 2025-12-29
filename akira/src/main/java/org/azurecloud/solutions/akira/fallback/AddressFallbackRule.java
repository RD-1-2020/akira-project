package org.azurecloud.solutions.akira.fallback;

public record AddressFallbackRule(
        String key,
        DockerHost dockerHost,
        int port,
        String fallbackValue
) {

}