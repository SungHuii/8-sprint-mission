package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotBlank;

public record UserStatusUpdatePayload(
    @NotBlank(message = "마지막 활동 시간은 필수입니다.")
    String newLastActiveAt
) {
}
