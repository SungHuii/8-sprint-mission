package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /* Message entity CRUD service
    * */

    // create
    default MessageResponse create(MessageCreateRequest request) {
        throw new UnsupportedOperationException("create not implemented");
    }

    // find
    default List<MessageResponse> findAllByChannelId(UUID channelId) {
        throw new UnsupportedOperationException("findAllByChannelId not implemented");
    }

    // update
    default MessageResponse update(MessageUpdateRequest request) {
        throw new UnsupportedOperationException("update not implemented");
    }

    // delete
    default void deleteById(UUID messageId) {
        throw new UnsupportedOperationException("deleteById not implemented");
    }

    /*
    Spring 이전 버전 코드
    */
    @Deprecated
    Message save(Message message);
    @Deprecated
    Message saveMessage(UUID authorId, UUID channelId, String messageContent, List<UUID> attachmentIds);
    @Deprecated
    Message updateMessage(Message message);
    @Deprecated
    boolean deleteMessage(UUID messageId);
    @Deprecated
    Message findById(UUID messageId);
    @Deprecated
    List<Message> findAll();
}
