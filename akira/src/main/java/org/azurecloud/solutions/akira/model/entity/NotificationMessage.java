package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

/**
 * Represents a notification message to be sent, with support for guaranteed delivery.
 * The message is stored in the database and its status is tracked.
 */
@Data
@Entity
public class NotificationMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The content of the notification message. */
    private String message;

    /** The current delivery status of the message. */
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable = false)
    private Status status;

    /** An identifier for the service that generated the message. */
    private String senderId;

    /** The timestamp when the message was created. */
    private LocalDateTime createdAt;
    /** The timestamp when the message was last updated. */
    private LocalDateTime updatedAt;

    /**
     * Enum representing the delivery status of a notification.
     */
    public enum Status {
        /** The message has been created but not yet sent. */
        CREATED,
        /** The message has been successfully delivered. */
        DELIVERED,
        /** An attempt to deliver the message has failed. */
        FAILED
    }
}
