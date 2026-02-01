package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface MessageService {

  // create
  MessageResponse create(MessageCreateRequest request);

  // find (Cursor Pagination)
  PageResponse<MessageResponse> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

  // update
  MessageResponse update(UUID messageId, MessageUpdateRequest request);

  // delete
  void deleteById(UUID messageId);
}
