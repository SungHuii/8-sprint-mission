package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.auth.AuthException;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;
import com.sprint.mission.discodeit.exception.enums.CommonErrorCode;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.enums.UserStatusErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  public AuthResponse login(LoginRequest request) {
    validateLoginRequest(request);
    log.info("로그인 요청: username={}", request.username());

    String username = request.username().trim();

    // 1. 사용자 조회 (없으면 USER_NOT_FOUND)
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    // 2. 비밀번호 검증 (틀리면 INVALID_CREDENTIALS)
    if (user.getPassword() == null || !user.getPassword().equals(request.password())) {
      throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
    }

    // 3. 유저 상태 조회 (없으면 USER_STATUS_NOT_FOUND)
    UserStatus status = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(() -> new UserStatusException(UserStatusErrorCode.USER_STATUS_NOT_FOUND));

    boolean isOnline = status.isOnline(Instant.now());

    log.info("로그인 성공: userId={}", user.getId());
    return userMapper.toAuthResponse(user, isOnline);
  }

  private void validateLoginRequest(LoginRequest request) {
    if (request == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "요청이 null입니다.");
    }
    if (request.username() == null || request.username().isBlank()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "사용자명은 필수입니다.");
    }
    if (request.password() == null || request.password().isBlank()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "비밀번호는 필수입니다.");
    }
  }
}
