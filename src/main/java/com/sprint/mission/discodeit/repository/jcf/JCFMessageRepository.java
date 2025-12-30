package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
    prefix = RepoProps.PREFIX,
    name = RepoProps.TYPE_NAME,
    havingValue = RepoProps.TYPE_JCF,
    matchIfMissing = true
)
public class JCFMessageRepository implements MessageRepository {

  private final Map<UUID, Message> data = new ConcurrentHashMap<>();

  @Override
  public Message save(Message message) {
    if (message.getAuthorId() == null) {
      System.out.println("유저 아이디가 유효하지 않습니다.");
      return null;
    } else if (message.getChannelId() == null) {
      System.out.println("채널 아이디가 유효하지 않습니다.");
      return null;
    } else if (message.getMessageContent() == null || message.getMessageContent().isEmpty()) {
      System.out.println("메세지를 입력해주세요.");
      return null;
    }
    data.put(message.getId(), message);
    return message;
  }

  @Override
  public Message updateMessage(Message message) {
    Message existingMessage = data.get(message.getId());
    if (existingMessage == null) {
      System.out.println("해당 메시지를 찾을 수 없습니다.");
      return null;
    }
    existingMessage.updateMessage(message.getMessageContent());
    System.out.println("메시지가 성공적으로 수정되었습니다.");

    return existingMessage;
  }

  @Override
  public boolean deleteMessage(UUID messageId) {
    Message messageRemoved = data.remove(messageId);
    if (messageRemoved == null) {
      System.out.println("해당 메시지를 찾을 수 없습니다.");
      return false;
    }
    System.out.println("메시지가 성공적으로 삭제되었습니다.");
    return true;
  }

  @Override
  public Optional<Message> findById(UUID messageId) {
    return Optional.ofNullable(data.get(messageId));
  }

  @Override
  public List<Message> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return data.values().stream()
        .filter(message -> channelId.equals(message.getChannelId()))
        .toList();
  }

  @Override
  public void deleteAllByChannelId(UUID channelId) {
    data.values().removeIf(message -> channelId.equals(message.getChannelId()));
  }
}
