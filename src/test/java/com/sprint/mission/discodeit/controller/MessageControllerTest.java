package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.controller.support.ControllerTestSupport;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.enums.MessageErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class MessageControllerTest extends ControllerTestSupport {

  @Test
  @DisplayName("메시지 생성 성공")
  void create_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId, null);
    MessageResponse response = new MessageResponse(UUID.randomUUID(), channelId, null, "Hello",
        Collections.emptyList(), Instant.now(), Instant.now());

    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    given(messageService.create(any(MessageCreateRequest.class))).willReturn(response);

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello"));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 내용 없음")
  void create_Fail_InvalidInput() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest("", UUID.randomUUID(),
        UUID.randomUUID(), null); // 내용 없음

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
        .andExpect(jsonPath("$.code").value("COMMON-INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.details.content").exists());
  }

  @Test
  @DisplayName("메시지 목록 조회 성공")
  void findAllByChannelId_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    MessageResponse msg = new MessageResponse(UUID.randomUUID(), channelId, null, "Hello",
        Collections.emptyList(), Instant.now(), Instant.now());
    PageResponse<MessageResponse> pageResponse = new PageResponse<>(List.of(msg), null, 1, false,
        null);

    given(messageService.findAllByChannelId(eq(channelId), any(), any(Pageable.class)))
        .willReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("Hello"));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void update_Fail_MessageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated");

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willThrow(new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MESSAGE-NOT_FOUND"));
  }
}
