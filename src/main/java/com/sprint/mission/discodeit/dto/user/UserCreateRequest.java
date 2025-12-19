package com.sprint.mission.discodeit.dto.user;

public record UserCreateRequest(
        String name,
        String nickname,
        String phoneNumber,
        String password,
        String email,
        BinaryContentCreateRequest profile // nullable
) {}

