package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.Duration;

/**
 * Represents the configuration for an HTTP(S) monitoring task.
 */
@Data
@Entity
public class HttpMonitorConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** A user-friendly name for the monitored target. */
    private String name;

    /** The URL to be monitored. */
    private String url;
    /** The interval at which the URL should be checked. */
    private Duration checkInterval;
    /** The maximum time to wait for a response. */
    private Duration timeout;

    @ManyToOne
    private NotifierConfig notifierConfig;

    /** Whether to ignore TLS/SSL certificate errors. */
    private boolean ignoreTlsErrors;
}
