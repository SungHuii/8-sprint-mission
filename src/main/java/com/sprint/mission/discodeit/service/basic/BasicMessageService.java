package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public  BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message save(Message message) {
        if (userRepository.findById(message.getUserId()) == null) {
            System.out.println("유저 아이디가 유효하지 않습니다.");
            return null;
        } else if (channelRepository.findById(message.getChannelId()) == null) {
            System.out.println("채널 아이디가 유효하지 않습니다.");
            return null;
        } else if (message.getMessage()== null || message.getMessage().isEmpty()){
            System.out.println("메세지를 입력해주세요.");
            return null;
        }
        return messageRepository.save(message);
    }

    @Override
    public Message saveMessage(UUID userId, UUID channelId, String content) {
        return new Message(userId, channelId, content);
    }

    @Override
    public Message updateMessage(Message message) {
        Message checkExisted = messageRepository.findById(message.getId());

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
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }
}
