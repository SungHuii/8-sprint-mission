package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  int getNumeric();               // 1001

  String getDomain();             // USER

  String getErrorKey();           // NOT_FOUND

  String getCode();               // USER-NOT_FOUND

  HttpStatus getHttpStatus();     // 404

  String getMessage();            // 유저를 찾을 수 없습니다.
}
