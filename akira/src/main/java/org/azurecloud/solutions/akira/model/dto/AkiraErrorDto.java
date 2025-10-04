package org.azurecloud.solutions.akira.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for returning structured error information to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AkiraErrorDto {
    /**
     * The unique application-specific error code.
     */
    private String errorCode;
    /**
     * A human-readable message describing the error.
     */
    private String message;
}
