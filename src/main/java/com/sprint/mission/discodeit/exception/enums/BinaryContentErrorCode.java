package com.sprint.mission.discodeit.exception.enums;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BinaryContentErrorCode implements ErrorCode {

  FILE_NOT_FOUND(4001, "NOT_FOUND", HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
  FILE_UPLOAD_FAILED(4002, "UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
  FILE_DOWNLOAD_FAILED(4003, "DOWNLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR,
      "파일 다운로드에 실패했습니다."),
  INVALID_FILE_SIZE(4004, "INVALID_FILE_SIZE", HttpStatus.BAD_REQUEST, "파일 크기가 너무 큽니다"),
  INVALID_FILE_TYPE(4005, "INVALID_FILE_TYPE", HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다.");

  private final int numeric;
  private final String errorKey;
  private final HttpStatus httpStatus;
  private final String message;

  @Override
  public String getDomain() {
    return "FILE";
  }

  @Override
  public String getCode() {
    return getDomain() + "-" + getErrorKey();
  }
}
