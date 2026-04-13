package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtInformation {

  private UserResponse userResponse;
  private String accessToken;
  private String refreshToken;

  // 토큰 로테이션 갱신
  public void rotateToken(String newAccessToken, String newRefreshToken) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
  }
}
