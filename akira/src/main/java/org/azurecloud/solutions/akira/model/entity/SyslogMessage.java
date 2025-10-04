package org.azurecloud.solutions.akira.model.entity;

import org.springframework.stereotype.Indexed;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a received Syslog message.
 * This entity is indexed for full-text search.
 */
@Data
@Entity
@Indexed
public class SyslogMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long monitorId;

    @ManyToOne
    private SyslogMonitorConfig source;
    
    /** The main content of the message, indexed for searching. */
    private String message;

    /** The timestamp when the message was received by the application. */
    private LocalDateTime receivedAt;
}
