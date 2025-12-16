package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    /* JCF(Java Collections Framework) 기반으로 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화
     * data 필드를 활용해서 CRUD 메소드 구현
     */
    private final Map<UUID, Message> data;

    // 심화 요구사항 : 서비스 간 의존성 주입
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message save(Message message) {
        if (userService.findById(message.getAuthorId()) == null) {
            System.out.println("유저 아이디가 유효하지 않습니다.");
            return null;
        } else if (channelService.findById(message.getChannelId()) == null) {
            System.out.println("채널 아이디가 유효하지 않습니다.");
            return null;
        } else if (message.getMessageContent()== null || message.getMessageContent().isEmpty()){
            System.out.println("메세지를 입력해주세요.");
            return null;
        }
            data.put(message.getId(), message);
            return message;
    }

    @Override
    public Message saveMessage(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
        if ((userService.findById(authorId)) == null) {
            System.out.println("유저 아이디가 유효하지 않습니다.");
            return null;
        } else if (channelService.findById(channelId) == null) {
            System.out.println("채널 아이디가 유효하지 않습니다.");
            return null;
        }
        return new Message(authorId, channelId, content, attachmentIds);
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
}
