package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /* Message entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */
    public Message createMessage(Message message);
    Message updateMessage(Message message);
    boolean deleteMessage(UUID messageId);
    Message getMessage(UUID messageId);
    List<Message> getAllMessages();

}
