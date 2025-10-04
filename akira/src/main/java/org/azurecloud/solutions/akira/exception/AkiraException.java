package org.azurecloud.solutions.akira.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AkiraException extends RuntimeException {

    private final AkiraErrorCode errorCode;
    private final HttpStatus httpStatus;

    public AkiraException(AkiraErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AkiraException(AkiraErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
