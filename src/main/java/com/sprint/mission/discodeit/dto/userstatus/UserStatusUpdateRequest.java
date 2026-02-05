package com.sprint.mission.discodeit.dto.userstatus;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
    @NotNull(message = "UserStatus ID는 필수입니다.")
    UUID userStatusId,

    @NotNull(message = "마지막 활동 시간은 필수입니다.")
    Instant lastActiveAt
) {
}
