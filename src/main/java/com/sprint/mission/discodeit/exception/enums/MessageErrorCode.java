package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MessageErrorCode implements ErrorCode {

  MESSAGE_NOT_FOUND(3001, "NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다."),
  MESSAGE_UPDATE_NOT_ALLOWED(3002, "UPDATE_NOT_ALLOWED", HttpStatus.FORBIDDEN,
      "메시지 수정 권한이 없습니다."), // 작성자만 수정 가능
  MESSAGE_DELETE_NOT_ALLOWED(3003, "DELETE_NOT_ALLOWED", HttpStatus.FORBIDDEN, "메시지 삭제 권한이 없습니다."),
  INVALID_MESSAGE_CONTENT(3004, "INVALID_CONTENT", HttpStatus.BAD_REQUEST,
      "메시지 내용이 비어있거나 유효하지 않습니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "MESSAGE";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
