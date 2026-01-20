package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;

// 프론트엔드에서 보내는 JSON 데이터 구조에 맞춤
public record UserCreateRequest(
    String email,
    String username,
    String password,
    BinaryContentCreateRequest profile
) {

}
