package org.azurecloud.solutions.akira.controller.config;

public final class Endpoints {

    private Endpoints() {}

    public static final String API_BASE = "/api";
    public static final String MESSAGES_SUFFIX = "/messages";

    // Notifiers & Notifications
    public static final String NOTIFIERS = API_BASE + "/notifiers";

    // Monitors
    public static final String MONITORS_HTTP = API_BASE + "/monitors/http";
    public static final String MONITORS_SYSLOG = API_BASE + "/monitors/syslog";
}
