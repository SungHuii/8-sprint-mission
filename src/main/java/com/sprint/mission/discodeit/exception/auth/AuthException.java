package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;

public class AuthException extends DiscodeitException {

  public AuthException(AuthErrorCode errorCode) {
    super(errorCode);
  }

  public AuthException(AuthErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
