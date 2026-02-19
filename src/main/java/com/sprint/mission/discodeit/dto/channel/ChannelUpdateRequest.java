package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.Size;

public record ChannelUpdateRequest(
    @Size(min = 2, max = 30, message = "채널 이름은 2자 이상 30자 이하이어야 합니다.")
    String newName,

    @Size(max = 100, message = "채널 설명은 100자 이하이어야 합니다.")
    String newDescription
) {
}
