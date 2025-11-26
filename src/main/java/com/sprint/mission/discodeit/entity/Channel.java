package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {

    /*
    * 고유아이디
    * 생성시간
    * 수정시간
    * 채널명
    * 채널설명
    * */
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String chName;
    private String chDescription;

    public Channel(String chName, String chDescription) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.chName = chName;
        this.chDescription = chDescription;
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

    public String getChName() {
        return chName;
    }

    public void updateChName(String chName) {
        this.chName = chName;
        renewUpdatedAt();
    }

    public String getChDescription() {
        return chDescription;
    }

    public void updateChDescription(String chDescription) {
        this.chDescription = chDescription;
        renewUpdatedAt();
    }

    private void renewUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
