package com.sprint.mission.discodeit.dto.auth;

import java.util.UUID;

public record AuthResponse(
    UUID id,
    String name,
    String nickname,
    String phoneNumber,
    String email,
    UUID profileId
) {

}

