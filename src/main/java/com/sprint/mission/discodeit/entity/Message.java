package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    /*
    * 직렬화 UID
    * 고유아이디
    * 생성시간
    * 수정시간
    * 유저 참조
    * 채널 참조
    * 메시지 내용
    * 첨부 파일 참조 리스트
    * */
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private final UUID authorId;
    private final UUID channelId;
    private String messageContent;
    private final List<UUID> attachmentIds;

    public Message(UUID authorId, UUID channelId, String messageContent, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.authorId = authorId;
        this.channelId = channelId;
        this.messageContent = messageContent;
        this.attachmentIds = (attachmentIds != null)
                ? new ArrayList<>(attachmentIds)
                : new ArrayList<>();
    }

    public void updateMessage(String messageContent) {
        this.messageContent = messageContent;
        renewUpdatedAt();
    }

    private void renewUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
