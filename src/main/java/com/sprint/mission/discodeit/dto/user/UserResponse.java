package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String nickname,
        String phoneNumber,
        String email,
        UUID profileId, // nullable
        boolean online
) {
}

