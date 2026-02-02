package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.entity.enums.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
    UUID id,
    ChannelType type,
    String name,                // PUBLIC 채널일 때만 값이 있음.
    String description,         // PUBLIC 채널일 때만 값이 있음.
    Instant lastMessageAt,      // 메시지가 없으면 null.
    List<UserSummaryResponse> participants   // PUBLIC 채널은 빈 리스트, PRIVATE 채널은 참여자 목록.
) {

}
