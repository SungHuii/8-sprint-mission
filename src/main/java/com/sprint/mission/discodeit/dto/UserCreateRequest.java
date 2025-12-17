package com.sprint.mission.discodeit.dto;

public record UserCreateRequest(
        String name,
        String nickname,
        String phoneNumber,
        String password,
        String email,
        BinaryContentCreateRequest profile // nullable
) {}
