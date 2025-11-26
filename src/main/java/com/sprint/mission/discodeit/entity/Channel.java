package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {

    /*
    * 고유아이디
    * 생성시간
    * 수정시간
    * 채널명
    * 채널설명
    * 채널이미지
    * */
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String chName;
    private String chDescription;
    private String chImageUrl;

    public Channel() {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
    }

    public Channel(String chName, String chDescription, String chImageUrl) {
        this.chName = chName;
        this.chDescription = chDescription;
        this.chImageUrl = chImageUrl;
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

    public String getChName() {
        return chName;
    }

    public void updateChName(String chName) {
        this.chName = chName;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getChDescription() {
        return chDescription;
    }

    public void updateChDescription(String chDescription) {
        this.chDescription = chDescription;
        this.updatedAt = System.currentTimeMillis();
    }

    public String getChImageUrl() {
        return chImageUrl;
    }

    public void updateChImageUrl(String chImageUrl) {
        this.chImageUrl = chImageUrl;
        this.updatedAt = System.currentTimeMillis();
    }
}
