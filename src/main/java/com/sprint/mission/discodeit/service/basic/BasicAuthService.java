package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.TokenRefreshResult;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
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

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  // updateUserRole() 호출 후 해당 유저의 세션 강제 만료
  @Override
  public TokenRefreshResult refresh(String refreshToken) {

    // 유효성 검사
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new DiscodeitException(AuthErrorCode.INVALID_TOKEN);
    }

    // Registry에 존재하는지 검사 (로그아웃된 토큰 차단)
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new DiscodeitException(AuthErrorCode.INVALID_TOKEN);
    }

    // userId 추출 후 DB 조회
    UUID userId = jwtTokenProvider.extractUserId(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new DiscodeitException(AuthErrorCode.INVALID_TOKEN));

    // Rotation 새 토큰 발급
    String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getUsername());
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

    // Registry에서 Rotation 돌리기
    UserResponse userResponse = userMapper.toUserResponse(user, isOnline(userId));
    JwtInformation newJwtInfo = new JwtInformation(userResponse, newAccessToken, newRefreshToken);
    jwtRegistry.rotateJwtInformation(refreshToken, newJwtInfo);

    JwtDto jwtDto = new JwtDto(userMapper.toUserResponse(user, isOnline(userId)), newAccessToken);

    return new TokenRefreshResult(jwtDto, newRefreshToken);
  }

  private boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
