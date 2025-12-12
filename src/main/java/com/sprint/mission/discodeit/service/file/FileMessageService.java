package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    /*
     * File IO를 통한 데이터 영속화
     * FileIO와 객체 직렬화를 활용해 메소드 구현
     * */
    private static final String FILE_PATH = "data/message.ser";
    private Map<UUID, Message> data;

    public FileMessageService() {
        this.data = new HashMap<>();
        loadFile();
    }



    @Override
    public Message save(Message message) {
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
        saveFile();
        return message;
    }

    @Override
    public Message saveMessage(UUID userId, UUID channelId, String content) {
        if (userId == null) {
            System.out.println("유저 아이디가 유효하지 않습니다.");
            return null;
        } else if (channelId == null) {
            System.out.println("채널 아이디가 유효하지 않습니다.");
            return null;
        }
        return new Message(userId, channelId, content);
    }

    @Override
    public Message updateMessage(Message message) {
        Message existingMessage = data.get(message.getId());
        if (existingMessage == null) {
            System.out.println("해당 메시지를 찾을 수 없습니다.");
            return null;
        }
        existingMessage.updateMessage(message.getMessage());
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

    /* 파일 저장 */
    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 파일 불러오기 */
    private void loadFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.data = (Map<UUID, Message>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
