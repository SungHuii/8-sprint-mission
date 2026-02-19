package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

  INVALID_INPUT_VALUE(9001, "INVALID_INPUT_VALUE", HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),
  INTERNAL_SERVER_ERROR(9002, "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR,
      "서버 내부 오류가 발생했습니다."),
  METHOD_NOT_ALLOWED(9003, "METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED,
      "허용되지 않은 HTTP 메서드 입니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "COMMON";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
