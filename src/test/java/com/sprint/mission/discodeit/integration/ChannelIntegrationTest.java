package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
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
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublic_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "Desc");

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("General"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - 이름 누락")
  void createPublic_Fail_InvalidInput() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "Desc");

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON-INVALID_INPUT_VALUE"));
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivate_Success() throws Exception {
    // given
    User user = userRepository.save(new User("user", "email", "pw"));
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(user.getId()));

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 존재하지 않는 참여자")
  void createPrivate_Fail_UserNotFound() throws Exception {
    // given
    UUID unknownUserId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(unknownUserId));

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER-NOT_FOUND"));
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_Success() throws Exception {
    // given
    Channel channel = channelRepository.save(Channel.ofPublic("OldName", "Desc"));
    ChannelUpdateRequest request = new ChannelUpdateRequest("NewName", null);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("NewName"));
  }

  @Test
  @DisplayName("채널 수정 실패 - 비공개 채널 수정 시도")
  void update_Fail_PrivateChannel() throws Exception {
    // given
    Channel channel = channelRepository.save(Channel.ofPrivate());
    ChannelUpdateRequest request = new ChannelUpdateRequest("NewName", null);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("CHANNEL-UPDATE_NOT_ALLOWED"));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_Success() throws Exception {
    // given
    Channel channel = channelRepository.save(Channel.ofPublic("Del", "Desc"));

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channel.getId()))
        .andExpect(status().isNoContent());
  }
}
