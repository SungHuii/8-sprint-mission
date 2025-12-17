package com.sprint.mission.discodeit.dto;

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
