package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /* Message entity CRUD service
    * */

    // create
    MessageResponse create(MessageCreateRequest request);

    // find
    List<MessageResponse> findAllByChannelId(UUID channelId);

    // update
    MessageResponse update(MessageUpdateRequest request);

    // delete
    void deleteById(UUID messageId);
}

