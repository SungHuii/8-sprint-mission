package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword,
    BinaryContentCreateRequest newProfile // nullable
) {

}
