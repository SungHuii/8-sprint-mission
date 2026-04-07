package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.TokenRefreshResult;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
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
  private final UserMapper userMapper;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public TokenRefreshResult refresh(String refreshToken) {

    // 유효성 검사
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new DiscodeitException(AuthErrorCode.INVALID_TOKEN);
    }

    // userId 추출 후 DB 조회
    UUID userId = jwtTokenProvider.extractUserId(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DiscodeitException(AuthErrorCode.INVALID_TOKEN));

    // Rotation 새 토큰 발급
    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getUsername());
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

    JwtDto jwtDto = new JwtDto(userMapper.toUserResponse(user, false), newAccessToken);

    return new TokenRefreshResult(jwtDto, newRefreshToken);
  }

}
