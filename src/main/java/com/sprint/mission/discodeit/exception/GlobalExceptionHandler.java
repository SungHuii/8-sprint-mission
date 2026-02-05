package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import com.sprint.mission.discodeit.exception.enums.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleDiscodeitException(DiscodeitException e) {
    log.error("DiscodeitException : code={}, message={}, details={}",
        e.getErrorCode().getCode(), e.getMessage(), e.getDetails());

    ErrorResponse response = ErrorResponse.of(
        e.getErrorCode(),
        e.getClass().getSimpleName(),
        e.getDetails()
    );

    return ResponseEntity.
        status(e.getErrorCode().getHttpStatus())
        .body(response);
  }

  // 그 외 위 예외에 걸리지 않는 모든 예외에 대해 500 에러 반환
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Unexpected Exception", e);
    ErrorResponse response = ErrorResponse.of(
        CommonErrorCode.INTERNAL_SERVER_ERROR,
        e.getClass().getSimpleName(),
        null
    );

    return ResponseEntity.status(response.status()).body(response);
  }

}
