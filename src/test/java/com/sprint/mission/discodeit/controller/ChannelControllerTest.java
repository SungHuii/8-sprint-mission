package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.enums.ChannelErrorCode;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublic_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "Desc");
    ChannelResponse response = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC, "General",
        "Desc", null, Collections.emptyList());

    given(channelService.createPublic(any(PublicChannelCreateRequest.class))).willReturn(response);

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
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "Desc"); // 이름 없음

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON-INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.details.name").exists());
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivate_Success() throws Exception {
    // given
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID()));
    ChannelResponse response = new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE, null,
        null, null, Collections.emptyList());

    given(channelService.createPrivate(any(PrivateChannelCreateRequest.class))).willReturn(
        response);

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("채널 수정 실패 - 비공개 채널 수정 시도")
  void update_Fail_PrivateChannel() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    ChannelUpdateRequest request = new ChannelUpdateRequest("NewName", null);

    given(channelService.update(eq(channelId), any(ChannelUpdateRequest.class)))
        .willThrow(new ChannelException(ChannelErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED));

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("CHANNEL-UPDATE_NOT_ALLOWED"));
  }
}
