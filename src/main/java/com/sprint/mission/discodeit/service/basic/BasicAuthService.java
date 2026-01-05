package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
  /*
   * AuthService 구현체
   * 사용자명과 비밀번호 기반 기본 인증
   */

  private final UserRepository userRepository;

  @Override
  public AuthResponse login(LoginRequest request) {
    validateLoginRequest(request);

    String username = request.username().trim();

    // username으로 nickname을 조회
    User user = userRepository.findByNickname(username)
        .filter(u -> u.getPassword() != null && u.getPassword().equals(request.password()))
        .orElseThrow(() -> new IllegalArgumentException("사용자명과 비밀번호가 일치하지 않습니다."));

    return toAuthResponse(user);
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

  private AuthResponse toAuthResponse(User user) {
    return new AuthResponse(
        user.getId(),
        user.getName(),
        user.getNickname(),
        user.getPhoneNumber(),
        user.getEmail(),
        user.getProfileId()
    );
  }
}
