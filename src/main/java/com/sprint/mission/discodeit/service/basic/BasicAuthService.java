package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final SessionRegistry sessionRegistry;

  // updateUserRole() 호출 후 해당 유저의 세션 강제 만료
  @Override
  public void invalidateUserSessions(UUID userId) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(principal ->
            principal instanceof DiscodeitUserDetails userDetails && userDetails.getUserResponse()
                .id().equals(userId))
        .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
        .forEach(SessionInformation::expireNow);
  }

}
