package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorCode;
import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 요청 파라미터/값 때문에 발생했을 때 400 에러 반환
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return build(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    // 현재 상태에서 허용되지 않거나 처리 불가능할 때 409 에러 반환
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        return build(ErrorCode.CONFLICT, e.getMessage());
    }

    // 리소스를 찾을 수 없을 때 404 에러 반환
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException e) {
        return build(ErrorCode.NOT_FOUND, e.getMessage());
    }

    // 그 외 위 예외에 걸리지 않는 모든 예외에 대해 500 에러 반환
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return build(ErrorCode.INTERNAL_ERROR, "Unexpected error occurred.");
    }

    // ErrorCode 기준으로 HttpStatus와 표준 에러 바디 생성
    private ResponseEntity<ErrorResponse> build(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.status())
                .body(new ErrorResponse(errorCode.code(), message, Instant.now()));
    }
}
