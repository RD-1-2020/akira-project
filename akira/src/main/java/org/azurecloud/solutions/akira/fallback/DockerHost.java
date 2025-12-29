package org.azurecloud.solutions.akira.fallback;

public enum DockerHost {

    AKIRA_APP("akira-app"),
    AKIRA_POSTGRES("akira-postgres")
    ;

    private final String hostname;

    DockerHost(String hostname) {
        this.hostname = hostname;
    }

    public String hostname() {
        return hostname;
    }
}