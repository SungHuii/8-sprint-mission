package com.sprint.mission.discodeit.dto.auth;

public record LoginRequest(
        String nickname,
        String password
) {
}

