package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String name,
        String nickname,
        String phoneNumber,
        String password,
        String email,
        BinaryContentCreateRequest newProfile // nullable
) {
}
