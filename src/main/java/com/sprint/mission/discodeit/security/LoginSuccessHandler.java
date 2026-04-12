package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    log.info("로그인 성공 처리 시작");

    // 응답 설정
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    if (authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {

      response.setStatus(HttpStatus.OK.value());
      UserResponse userResponse = userDetails.getUserResponse();

      objectMapper.writeValue(response.getWriter(), userResponse);
      log.info("로그인 성공: {}", userResponse.username());
    } else {

      // 예상치 못한 인증 객체일 경우의 방어 로직
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.getWriter().write("{\"message\": \"서버 내부 인증 처리 오류\"}");
      log.error("예상하지 못한 Principal 타입: {}", authentication.getPrincipal().getClass());
    }
  }
}
