package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import java.util.UUID;

public record AuthResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online
) {

}
