package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.AuthApi;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  @GetMapping("csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);

    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser(
      // Spring Security가 현재 세션(JSESSIONID)을 확인해서 로그인된 유저 객체를 주입시킴
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {

    // 필터를 뚫고 permitAll로 들어올 경우 방어 로직
    if (userDetails == null) {
      throw new DiscodeitException(AuthErrorCode.AUTHENTICATION_FAILED);
    }

    return ResponseEntity.ok(userDetails.getUserResponse());
  }
}
