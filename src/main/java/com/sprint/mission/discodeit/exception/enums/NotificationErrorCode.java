package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

  NOTIFICATION_NOT_FOUND(8001, "NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "NOTIFICATION";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
