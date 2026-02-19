package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.controller.support.ControllerTestSupport;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class UserControllerTest extends ControllerTestSupport {

  @Test
  @DisplayName("회원가입 성공")
  void create_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("test@test.com", "testuser", "password",
        null);
    UserResponse response = new UserResponse(UUID.randomUUID(), "testuser", "test@test.com", null,
        true);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    given(userService.create(any(UserCreateRequest.class))).willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("회원가입 실패 - 이메일 유효성 검사 실패")
  void create_Fail_InvalidInput() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("invalid-email", "testuser", "password",
        null);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON-INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.details.email").exists()); // 이메일 필드 에러 확인
  }

  @Test
  @DisplayName("유저 정보 수정 성공")
  void update_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUsername", null, null, null);
    UserResponse response = new UserResponse(userId, "newUsername", "test@test.com", null, true);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    given(userService.update(eq(userId), any(UserUpdateRequest.class))).willReturn(response);

    // when & then
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newUsername"));
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 존재하지 않는 유저")
  void update_Fail_UserNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUsername", null, null, null);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    given(userService.update(eq(userId), any(UserUpdateRequest.class)))
        .willThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

    // when & then
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER-NOT_FOUND"));
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());
  }
}
