package com.sprint.mission.discodeit.security;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

  // 사용자 본인인지 검증하는 메서드
  public boolean isOwner(Authentication authentication, UUID userId) {
    if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }
    return userDetails.getUserResponse().id().equals(userId);
  }
}
