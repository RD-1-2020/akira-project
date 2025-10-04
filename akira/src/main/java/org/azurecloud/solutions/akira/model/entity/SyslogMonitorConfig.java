package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the configuration for a Syslog monitoring source.
 */
@Data
@Entity
public class SyslogMonitorConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** A user-friendly name for the monitored source. */
    private String name;

    /** The URL (IP address) of the Syslog source being monitored. */
    private String sourceUrl;
    /** The maximum interval of inactivity before an alert is triggered. */
    private Duration noMessageInterval;

    @ManyToOne
    private NotifierConfig notifierConfig;

    /** The timestamp when the last message was received from this source. */
    private LocalDateTime lastMessageAt;

    @OneToMany(mappedBy = "source")
    private List<SyslogMessage> messages;
}
