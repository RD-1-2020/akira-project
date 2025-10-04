package org.azurecloud.solutions.akira.service.monitor;

/**
 * A common interface for all monitoring services.
 */
public interface AkiraMonitoring {
    /**
     * Triggers the monitoring check.
     */
    void check();
}
