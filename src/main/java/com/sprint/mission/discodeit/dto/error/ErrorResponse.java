package com.sprint.mission.discodeit.dto.error;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    String code,
    String message,
    Map<String, Object> details,
    String exceptionType,
    int status
) {

  public static ErrorResponse of(ErrorCode errorCode, String exceptionType,
      Map<String, Object> details) {
    return new ErrorResponse(
        Instant.now(),
        errorCode.getCode(),
        errorCode.getMessage(),
        details,
        exceptionType,
        errorCode.getHttpStatus().value()
    );
  }

  // details 없는 버전
  public static ErrorResponse of(ErrorCode errorCode, String exceptionType) {
    return of(errorCode, exceptionType, null);
  }
}
