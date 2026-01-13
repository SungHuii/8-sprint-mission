package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관리 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인", description = "사용자명(username)과 비밀번호로 로그인합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (필수 값 누락)", content = @Content),
      @ApiResponse(responseCode = "401", description = "인증 실패 (사용자명 또는 비밀번호 불일치)", content = @Content)
  })
  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest request) {
    return authService.login(request);
  }
}
