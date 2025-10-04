package org.azurecloud.solutions.akira.controller.config;

import org.azurecloud.solutions.akira.exception.AkiraException;
import org.azurecloud.solutions.akira.model.dto.AkiraErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A global controller advice to handle application-specific exceptions.
 * It catches {@link AkiraException} and transforms it into a structured {@link AkiraErrorDto} response.
 */
@ControllerAdvice
public class AkiraAdviceController {

    /**
     * Handles {@link AkiraException} and returns a ResponseEntity with the appropriate HTTP status and error DTO.
     * @param e The caught AkiraException.
     * @return A ResponseEntity containing the AkiraErrorDto.
     */
    @ExceptionHandler(AkiraException.class)
    public ResponseEntity<AkiraErrorDto> handleAkiraException(AkiraException e) {
        AkiraErrorDto errorDto = new AkiraErrorDto(e.getErrorCode().getCode(), e.getMessage());
        return new ResponseEntity<>(errorDto, e.getHttpStatus());
    }
}
