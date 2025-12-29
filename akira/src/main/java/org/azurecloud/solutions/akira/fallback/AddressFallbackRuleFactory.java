package org.azurecloud.solutions.akira.fallback;

public final class AddressFallbackRuleFactory {

    private static final String LOCALHOST = "127.0.0.1:";
    private static final String JDBC = "jdbc:postgresql://";

    private AddressFallbackRuleFactory() {
    }

    public static AddressFallbackRule jdbc(String key, DockerHost dockerHost, int port) {
        return new AddressFallbackRule(key, dockerHost, port, JDBC + LOCALHOST + port + "/akira?sslmode=require");
    }
}