package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(
    prefix = RepoProps.PREFIX,
    name = RepoProps.TYPE_NAME,
    havingValue = RepoProps.TYPE_FILE
)
public class FileMessageRepository implements MessageRepository {

  private final String filePath;
  private Map<UUID, Message> data;

  public FileMessageRepository(@Value(RepoProps.FILE_DIRECTORY_PLACEHOLDER) String baseDir) {
    this.data = new ConcurrentHashMap<>();
    this.filePath = new File(baseDir, "messageRepo.ser").getPath();
    loadFile();
  }


  @Override
  public Message save(Message message) {
    if (message.getAuthorId() == null) {
      System.out.println("작성자 아이디가 유효하지 않습니다.");
      return null;
    } else if (message.getChannelId() == null) {
      System.out.println("채널 아이디가 유효하지 않습니다.");
      return null;
    } else if (message.getMessageContent() == null || message.getMessageContent().isEmpty()) {
      System.out.println("메세지를 입력해주세요.");
      return null;
    }
    data.put(message.getId(), message);
    saveFile();
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
    saveFile();
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
    saveFile();
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
    saveFile();
  }

  private void saveFile() {
    ensureParentDir();
    try (ObjectOutputStream oos =
        new ObjectOutputStream(new FileOutputStream(filePath))) {
      oos.writeObject(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadFile() {
    File file = new File(filePath);
    if (!file.exists()) {
      return;
    }

    try (ObjectInputStream ois =
        new ObjectInputStream(new FileInputStream(filePath))) {

      Object obj = ois.readObject();
      if (obj instanceof Map) {
        data = (Map<UUID, Message>) obj;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void ensureParentDir() {
    /* 상위 파일 디렉토리가 있는지 확인. 없으면 생성 */
    File file = new File(filePath);
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }
  }
}
