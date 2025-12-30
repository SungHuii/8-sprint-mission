package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;

public record UserCreateRequest(
    String name,
    String nickname,
    String phoneNumber,
    String password,
    String email,
    BinaryContentCreateRequest profile // nullable
) {

}
