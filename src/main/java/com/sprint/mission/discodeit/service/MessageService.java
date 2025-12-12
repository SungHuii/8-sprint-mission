package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    /* Message entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */
    @Deprecated
    Message save(Message message);
    Message saveMessage(UUID userId, UUID channelId, String content);
    Message updateMessage(Message message);
    boolean deleteMessage(UUID messageId);
    Message findById(UUID messageId);
    List<Message> findAll();

}
