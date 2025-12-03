package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    public  BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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
        return messageRepository.createMessage(message);
    }

    @Override
    public Message updateMessage(Message message) {
        Message checkExisted = messageRepository.getMessage(message.getId());

        if (checkExisted == null) {
            System.out.println("해당 메세지가 존재하지 않습니다.");
            return null;
        }
        checkExisted.updateMessage(message.getMessage());
        return messageRepository.updateMessage(message);
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        return messageRepository.deleteMessage(messageId);
    }

    @Override
    public Message getMessage(UUID messageId) {
        return messageRepository.getMessage(messageId);
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.getAllMessages();
    }
}
