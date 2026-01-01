package com.sprint.mission.discodeit.dto.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),                // 400 Error
    CONFLICT(HttpStatus.CONFLICT),                      // 409 Error
    NOT_FOUND(HttpStatus.NOT_FOUND),                    // 404 Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);   // 500 Error

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return name();
    }
}
