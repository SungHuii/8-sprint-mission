package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    Message createMessage(Message message);
    Message updateMessage(Message message);
    boolean deleteMessage(UUID messageId);
    Message getMessage(UUID messageId);
    List<Message> getAllMessages();
}
