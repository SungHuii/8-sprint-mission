package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.enums.UserErrorCode;

public class UserNotFoundException extends UserException {

  public UserNotFoundException() {
    super(UserErrorCode.USER_NOT_FOUND);
  }

  public UserNotFoundException(String message) {
    super(UserErrorCode.USER_NOT_FOUND, message);
  }

}
