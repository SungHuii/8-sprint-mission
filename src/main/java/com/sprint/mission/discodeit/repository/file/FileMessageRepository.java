package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "dataRepo/messageRepo.ser";
    private Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = new HashMap<>();
        load();
    }

    private void save() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
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

    @Override
    public Message createMessage(Message message) {
        if (message.getUserId() == null) {
            System.out.println("유저 아이디가 유효하지 않습니다.");
            return null;
        } else if (message.getChannelId() == null) {
            System.out.println("채널 아이디가 유효하지 않습니다.");
            return null;
        } else if (message.getMessage()== null || message.getMessage().isEmpty()){
            System.out.println("메세지를 입력해주세요.");
            return null;
        }
        data.put(message.getId(), message);
        save();
        return message;
    }

    @Override
    public Message updateMessage(Message message) {
        Message existingMessage = data.get(message.getId());
        if (existingMessage != null) {
            existingMessage.updateMessage(message.getMessage());
            save();
            return existingMessage;
        } else {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
            return null;
        }
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        Message messageRemoved = data.remove(messageId);
        if (messageRemoved != null) {
            System.out.println("메시지가 성공적으로 삭제되었습니다.");
            save();
            return true;
        } else {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
            return false;
        }
    }

    @Override
    public Message getMessage(UUID messageId) {
        Message message = data.get(messageId);
        if (message == null) {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
            return null;
        }
        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        List<Message> allMessages = new ArrayList<>(data.values());
        return allMessages;
    }
}
