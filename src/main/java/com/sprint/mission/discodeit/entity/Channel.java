package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.enums.ChannelType;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    /*
    * 직렬화 UID
    * 고유아이디
    * 생성시간
    * 수정시간
    * 채널명 public 채널 전용
    * 채널설명 public 채널 전용
    * 채널타입 (enum: PUBLIC, PRIVATE)
    * Private 채널 전용
    * */
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String chName;
    private String chDescription;
    private final ChannelType chType;
    private final List<UUID> participantIds;

    // PUBLIC 채널 생성자
    public Channel(String chName, String chDescription) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.chType = ChannelType.PUBLIC;
        this.chName = chName;
        this.chDescription = chDescription;
        this.participantIds = List.of();
    }

    // PRIVATE 채널 생성자
    public Channel(List<UUID> participantIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.chType = ChannelType.PRIVATE;
        this.chName = null;
        this.chDescription = null;
        this.participantIds = validateParticipants(participantIds);
    }

    public void updateChName(String chName) {
        validateChannelType();
        this.chName = chName;
        renewUpdatedAt();
    }

    public void updateChDescription(String chDescription) {
        this.chDescription = chDescription;
        renewUpdatedAt();
    }

    private void renewUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    private static List<UUID> validateParticipants(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Private 채널은 참가자 목록이 필요합니다.");
        }
        return List.copyOf(ids);
    }

    private void validateChannelType() {
        if (this.chType == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }
    }
}
