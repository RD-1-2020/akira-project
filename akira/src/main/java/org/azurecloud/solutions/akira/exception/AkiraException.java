package org.azurecloud.solutions.akira.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@Getter
public class AkiraException extends RuntimeException {

    private final @NonNull AkiraErrorCode errorCode;
    private final @NonNull HttpStatus httpStatus;

    public AkiraException(@NonNull AkiraErrorCode errorCode, @NonNull HttpStatus httpStatus, @NonNull String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AkiraException(AkiraErrorCode errorCode, @NonNull HttpStatus httpStatus) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
