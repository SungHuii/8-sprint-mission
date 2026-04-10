package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.enums.Role;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    boolean online,
    Role role
) {

}
