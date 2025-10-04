package org.azurecloud.solutions.akira.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of application-specific error codes.
 * Each code has a unique identifier and a human-readable description.
 */
@Getter
@RequiredArgsConstructor
public enum AkiraErrorCode {

    /** When input validation fails. */
    VALIDATION_ERROR("AK-40001", "Invalid input provided."),

    /** When a user is not authorized to perform an action. */
    ACCESS_DENIED("AK-40301", "Access is denied."),

    /** When a requested resource cannot be found. */
    RESOURCE_NOT_FOUND("AK-40401", "The requested resource was not found."),

    /** A generic error for unexpected server-side issues. */
    INTERNAL_SERVER_ERROR("AK-50001", "An internal server error occurred."),
    /** When sending a notification fails. */
    NOTIFICATION_SEND_FAILED("AK-50002", "Failed to send notification.");

    /** The unique error code string. */
    private final String code;
    /** A description of the error. */
    private final String description;
}
