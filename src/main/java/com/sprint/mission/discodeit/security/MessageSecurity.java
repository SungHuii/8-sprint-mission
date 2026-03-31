package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

  private final MessageRepository messageRepository;

  // 작성자 본인인지 검증하는 메서드
  public boolean isAuthor(Authentication authentication, UUID messageId) {

    if (!(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return false;
    }

    return messageRepository.findById(messageId)
        .map(message ->
            message
                .getAuthor().getId()
                .equals(userDetails.getUserResponse().id()))
        .orElse(false);
  }
}
