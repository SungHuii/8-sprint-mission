package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserStatusErrorCode implements ErrorCode {

  USER_STATUS_NOT_FOUND(6001, "NOT_FOUND", HttpStatus.NOT_FOUND, "사용자 상태 정보를 찾을 수 없습니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "USER_STATUS";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
