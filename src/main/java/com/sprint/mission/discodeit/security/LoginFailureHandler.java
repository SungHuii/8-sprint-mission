package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.error.ErrorResponse;
import com.sprint.mission.discodeit.exception.enums.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {

    log.warn("로그인 실패 : {}", exception.getMessage());

    // 에러 응답 객체 생성
    ErrorResponse errorResponse = ErrorResponse.of(AuthErrorCode.INVALID_CREDENTIALS,
        exception.getClass().getSimpleName());

    // HTTP 응답 설정
    response.setStatus(AuthErrorCode.INVALID_CREDENTIALS.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    // JSON 변환 출력
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
