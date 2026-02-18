package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.integration.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class UserIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  @DisplayName("회원가입 성공")
  void createUser_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("test@test.com", "testuser", "password",
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
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("회원가입 실패 - 중복된 이메일")
  void createUser_Fail_Duplicate() throws Exception {
    // given
    User user = userRepository.save(new User("existing", "test@test.com", "pw"));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    UserCreateRequest request = new UserCreateRequest("test@test.com", "newuser", "password", null);
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
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER-DUPLICATE_EMAIL"));
  }

  @Test
  @DisplayName("유저 정보 수정 성공")
  void updateUser_Success() throws Exception {
    // given
    User user = userRepository.save(new User("oldName", "old@test.com", "pw"));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    UserUpdateRequest request = new UserUpdateRequest("newName", null, null, null);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", user.getId())
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newName"));
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 존재하지 않는 유저")
  void updateUser_Fail_UserNotFound() throws Exception {
    // given
    UUID unknownUserId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newName", null, null, null);

    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", unknownUserId)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER-NOT_FOUND"));
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void deleteUser_Success() throws Exception {
    // given
    User user = userRepository.save(new User("delUser", "del@test.com", "pw"));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", user.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("전체 유저 조회 성공")
  void findAll_Success() throws Exception {
    // given
    User u1 = userRepository.save(new User("u1", "u1@test.com", "pw"));
    userStatusRepository.save(new UserStatus(u1, Instant.now()));

    User u2 = userRepository.save(new User("u2", "u2@test.com", "pw"));
    userStatusRepository.save(new UserStatus(u2, Instant.now()));

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[?(@.username == 'u1')]").exists())
        .andExpect(jsonPath("$[?(@.username == 'u2')]").exists());
  }

  @Test
  @DisplayName("전체 유저 조회 - 유저가 없을 때 빈 리스트 반환")
  void findAll_Empty() throws Exception {
    // given
    // 아무것도 저장하지 않음

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
