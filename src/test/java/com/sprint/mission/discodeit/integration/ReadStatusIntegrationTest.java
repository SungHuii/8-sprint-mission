package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
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
class ReadStatusIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("읽음 상태 생성 성공")
  void create_Success() throws Exception {
    // given
    User user = userRepository.save(new User("user", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));

    ReadStatusCreateRequest request = new ReadStatusCreateRequest(user.getId(), channel.getId(), null);

    // when & then
    mockMvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.lastReadAt").exists());
  }

  @Test
  @DisplayName("읽음 상태 생성 실패 - 중복 생성")
  void create_Fail_Duplicate() throws Exception {
    // given
    User user = userRepository.save(new User("user", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));
    readStatusRepository.save(new ReadStatus(user, channel, Instant.now()));

    ReadStatusCreateRequest request = new ReadStatusCreateRequest(user.getId(), channel.getId(), Instant.now());

    // when & then
    mockMvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("READ_STATUS-DUPLICATE"));
  }

  @Test
  @DisplayName("읽음 상태 수정 성공")
  void update_Success() throws Exception {
    // given
    User user = userRepository.save(new User("user", "email", "pw"));
    Channel channel = channelRepository.save(Channel.ofPublic("ch", "desc"));
    ReadStatus readStatus = readStatusRepository.save(new ReadStatus(user, channel, Instant.now()));

    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now().plusSeconds(10));

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", readStatus.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("읽음 상태 수정 실패 - 존재하지 않는 ID")
  void update_Fail_ReadStatusNotFound() throws Exception {
    // given
    UUID unknownId = UUID.randomUUID();
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(Instant.now());

    // when & then
    mockMvc.perform(patch("/api/readStatuses/{readStatusId}", unknownId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("READ_STATUS-NOT_FOUND"));
  }

  @Test
  @DisplayName("유저별 읽음 상태 목록 조회 성공")
  void findAllByUserId_Success() throws Exception {
    // given
    User user = userRepository.save(new User("user", "email", "pw"));
    Channel ch1 = channelRepository.save(Channel.ofPublic("ch1", "desc"));
    Channel ch2 = channelRepository.save(Channel.ofPublic("ch2", "desc"));
    readStatusRepository.save(new ReadStatus(user, ch1, Instant.now()));
    readStatusRepository.save(new ReadStatus(user, ch2, Instant.now()));

    // when & then
    mockMvc.perform(get("/api/readStatuses")
            .param("userId", user.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }
}
