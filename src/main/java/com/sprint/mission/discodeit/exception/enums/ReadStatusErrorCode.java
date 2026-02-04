package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReadStatusErrorCode implements ErrorCode {

  READ_STATUS_NOT_FOUND(5001, "NOT_FOUND", HttpStatus.NOT_FOUND, "읽음 상태 정보를 찾을 수 없습니다."),
  DUPLICATE_READ_STATUS(5002, "DUPLICATE", HttpStatus.CONFLICT, "이미 존재하는 읽음 상태입니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "READ_STATUS";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
