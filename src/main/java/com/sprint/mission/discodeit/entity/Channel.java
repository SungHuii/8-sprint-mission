package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    /*
    * 직렬화 UID
    * 고유아이디
    * 생성시간
    * 수정시간
    * 채널명
    * 채널설명
    * */
    private static final long serialVersionUID = 1L;
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

    public void updateChName(String chName) {
        this.chName = chName;
        renewUpdatedAt();
    }

    public void updateChDescription(String chDescription) {
        this.chDescription = chDescription;
        renewUpdatedAt();
    }

    private void renewUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
