package com.sprint.mission.discodeit.exception.enums;


import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

  USER_NOT_FOUND(1001, "NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
  DUPLICATE_USERNAME(1002, "DUPLICATE_USERNAME", HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다."),
  DUPLICATE_EMAIL(1003, "DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "USER";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }

}
