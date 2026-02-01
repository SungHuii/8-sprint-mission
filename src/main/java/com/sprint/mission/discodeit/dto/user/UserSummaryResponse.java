package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import java.util.UUID;

public record UserSummaryResponse(
    UUID id,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online
) {

}
