package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleUpdateRequest(
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @NotNull(message = "새로운 권한은 필수입니다.")
    Role newRole
) {

}
