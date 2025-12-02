package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {

    /*
    * 직렬화 UID
    * 고유아이디
    * 생성시간
    * 수정시간
    * 유저 참조
    * 채널 참조
    * 메시지 내용
    * */
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private final UUID userId;
    private final UUID channelId;
    private String message;

    public Message(UUID userId, UUID channelId, String message) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.userId = userId;
        this.channelId = channelId;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
        renewUpdatedAt();
    }

    public void renewUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
