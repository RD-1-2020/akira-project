package org.azurecloud.solutions.akira.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

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
