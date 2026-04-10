package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface AuthService {

  // updateUserRole() 호출 후 해당 유저의 세션 강제 만료
  void invalidateUserSessions(UUID userId);
}