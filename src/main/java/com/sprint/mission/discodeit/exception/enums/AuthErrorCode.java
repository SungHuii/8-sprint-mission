package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

  INVALID_CREDENTIALS(4001, "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED,
      "사용자명 또는 비밀번호가 올바르지 않습니다."),
  ACCOUNT_DISABLED(4002, "ACCOUNT_DISABLED", HttpStatus.UNAUTHORIZED, "계정이 비활성화 되었습니다."),
  ACCESS_DENIED(4003, "ACCESS_DENIED", HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "AUTH";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
