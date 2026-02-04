package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChannelErrorCode implements ErrorCode {

  CHANNEL_NOT_FOUND(2001, "NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 채널입니다."),
  PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED(2002, "UPDATE_NOT_ALLOWED", HttpStatus.BAD_REQUEST,
      "비공개 채널은 수정할 수 없습니다."),
  DUPLICATE_CHANNEL_NAME(2003, "DUPLICATE_CHANNEL_NAME", HttpStatus.CONFLICT, "이미 존재하는 채널명입니다."),
  INVALID_CHANNEL_TYPE(2004, "INVALID_CHANNEL_TYPE", HttpStatus.BAD_REQUEST, "유효하지 않은 채널 타입입니다."),
  CHANNEL_ACCESS_DENIED(2005, "ACCESS_DENIED", HttpStatus.FORBIDDEN, "채널에 접근할 권한이 없습니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "CHANNEL";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }

}
