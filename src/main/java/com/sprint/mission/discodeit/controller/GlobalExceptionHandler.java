package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        return build(HttpStatus.CONFLICT, "CONFLICT", e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error occurred.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, message, Instant.now()));
    }
}
