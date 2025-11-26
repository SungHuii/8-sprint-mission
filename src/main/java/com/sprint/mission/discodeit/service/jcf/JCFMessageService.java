package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    /* JCF(Java Collections Framework) 기반으로 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화
     * data 필드를 활용해서 CRUD 메소드 구현
     */
    private final Map<UUID, Message> data;
    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String message) {
        if (userId == null) {
            System.out.println("유저 아이디가 유효하지 않습니다.");
            return null;
        } else if (channelId == null) {
            System.out.println("채널 아이디가 유효하지 않습니다.");
            return null;
        } else if (message == null){
            System.out.println("메세지를 입력해주세요.");
            return null;
        } else {
            Message msg = new Message(userId, channelId, message);
            data.put(msg.getId(), msg);
            return msg;
        }
    }

    @Override
    public Message updateMessage(Message message) {
        Message existingMessage = data.get(message.getId());
        if (existingMessage != null) {
            existingMessage.updateMessage(message.getMessage());
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
