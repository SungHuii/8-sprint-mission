package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  @DisplayName("로그인 성공")
  void login_Success() throws Exception {
    // given
    User user = userRepository.save(new User("testuser", "test@test.com", "password"));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    LoginRequest request = new LoginRequest("testuser", "password");

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("로그인 실패 - 존재하지 않는 사용자")
  void login_Fail_UserNotFound() throws Exception {
    // given
    LoginRequest request = new LoginRequest("unknown", "password");

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER-NOT_FOUND"));
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치")
  void login_Fail_WrongPassword() throws Exception {
    // given
    User user = userRepository.save(new User("testuser", "test@test.com", "password"));
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    LoginRequest request = new LoginRequest("testuser", "wrongPassword");

    // when & then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized()) // 401 Unauthorized
        .andExpect(jsonPath("$.code").value("AUTH-INVALID_CREDENTIALS"));
  }
}
