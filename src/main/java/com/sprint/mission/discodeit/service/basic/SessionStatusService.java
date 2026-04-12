package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionStatusService {

  private final SessionRegistry sessionRegistry;

  public boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails userDetails
            && userDetails.getUserResponse().id().equals(userId))
        .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
        .anyMatch(session -> !session.isExpired());
  }

}
