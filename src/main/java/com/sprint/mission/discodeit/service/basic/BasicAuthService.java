package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {
  /*
   * AuthService 구현체
   * 사용자명과 비밀번호 기반 기본 인증
   */

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  public AuthResponse login(LoginRequest request) {
    validateLoginRequest(request);

    String username = request.username().trim();

    User user = userRepository.findByUsername(username)
        .filter(u -> u.getPassword() != null && u.getPassword().equals(request.password()))
        .orElseThrow(() -> new IllegalArgumentException("사용자명과 비밀번호가 일치하지 않습니다."));

    UserStatus status = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(() -> new IllegalStateException("유저 상태가 존재하지 않습니다. userId=" + user.getId()));

    boolean isOnline = status.isOnline(Instant.now());

    return userMapper.toAuthResponse(user, isOnline);
  }

  private void validateLoginRequest(LoginRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.username() == null || request.username().isBlank()) {
      throw new IllegalArgumentException("사용자명은 필수입니다.");
    }
    if (request.password() == null || request.password().isBlank()) {
      throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }
  }
}
