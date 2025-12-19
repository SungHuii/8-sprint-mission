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

    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private String chName;
    private String chDescription;
    private final ChannelType chType;
    private final List<UUID> participantIds;

    // PUBLIC 채널 생성
    public Channel(String chName, String chDescription) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.chType = ChannelType.PUBLIC;
        this.chName = chName;
        this.chDescription = chDescription;
        this.participantIds = List.of();
    }

    // PRIVATE 채널 생성
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
            throw new IllegalArgumentException("비공개 채널은 참가자 목록이 필요합니다.");
        }
        return List.copyOf(ids);
    }

    private void validateChannelType() {
        if (this.chType == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널은 수정할 수 없습니다.");
        }
    }
}