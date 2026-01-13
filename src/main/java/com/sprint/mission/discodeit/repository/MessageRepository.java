package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

  Message save(Message message);

  Message updateMessage(Message message);

  boolean deleteMessage(UUID messageId);

  Optional<Message> findById(UUID messageId);

  List<Message> findAll();

  List<Message> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
