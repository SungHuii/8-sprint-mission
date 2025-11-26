package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {

    /*
    * 고유아이디
    * 생성시간
    * 수정시간
    * 유저 참조
    * 채널 참조
    * 메시지 내용
    * */
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private UUID userId;
    private UUID channelId;
    private String message;

    public Message() {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
    }

    public Message(UUID userId, UUID channelId, String message) {
        this.userId = userId;
        this.channelId = channelId;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void updateUserId(UUID userId) {
        this.userId = userId;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
        this.updatedAt = System.currentTimeMillis();
    }
}
