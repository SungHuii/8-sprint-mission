package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.enums.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        ChannelType type,
        String name,                // PUBLIC 채널의 경우만 값이 있음
        String description,         // PUBLIC 채널의 경우만 값이 있음
        Instant lastMessageAt,      // 메시지가 없다면 null
        List<UUID> participantIds   // PUBLIC 채널의 경우 빈 리스트 / PRIVATE 채널의 경우 참여자 목록
) {
}
