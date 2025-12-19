package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "dataRepo/messageRepo.ser";
    private Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = new HashMap<>();
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
        } else if (message.getMessageContent()== null || message.getMessageContent().isEmpty()){
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
    public Message findById(UUID messageId) {
        Message message = data.get(messageId);
        if (message == null) {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
            return null;
        }
        return message;
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
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_PATH))) {

            Object obj = ois.readObject();
            if (obj instanceof Map) {
                data = (Map<UUID, Message>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
