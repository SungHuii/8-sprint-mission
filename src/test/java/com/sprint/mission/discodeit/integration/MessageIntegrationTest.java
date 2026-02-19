package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.integration.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class MessageIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_Success() throws Exception {
    // given
    User author = userRepository.save(new User("author", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));

    MessageCreateRequest request = new MessageCreateRequest("Hello", channel.getId(),
        author.getId(), null);
    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello"));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 내용 누락")
  void create_Fail_InvalidInput() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest("", UUID.randomUUID(),
        UUID.randomUUID(), null); // 빈 내용
    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON-INVALID_INPUT_VALUE"));
  }

  @Test
  @DisplayName("메시지 목록 조회 성공")
  void findAllByChannelId_Success() throws Exception {
    // given
    User author = userRepository.save(new User("author", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));
    messageRepository.save(new Message(author, channel, "msg1", Collections.emptyList()));
    messageRepository.save(new Message(author, channel, "msg2", Collections.emptyList()));

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channel.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  @DisplayName("메시지 목록 조회 실패 - 존재하지 않는 채널")
  void findAllByChannelId_Fail_ChannelNotFound() throws Exception {
    // given
    UUID unknownChannelId = UUID.randomUUID();

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", unknownChannelId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CHANNEL-NOT_FOUND"));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_Success() throws Exception {
    // given
    User author = userRepository.save(new User("author", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));
    Message message = messageRepository.save(
        new Message(author, channel, "Old", Collections.emptyList()));

    MessageUpdateRequest request = new MessageUpdateRequest("New");

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", message.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("New"));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void update_Fail_MessageNotFound() throws Exception {
    // given
    UUID unknownMessageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("New");

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", unknownMessageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE-NOT_FOUND"));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_Success() throws Exception {
    // given
    User author = userRepository.save(new User("author", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));
    Message message = messageRepository.save(
        new Message(author, channel, "Del", Collections.emptyList()));

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", message.getId()))
        .andExpect(status().isNoContent());
  }
}
