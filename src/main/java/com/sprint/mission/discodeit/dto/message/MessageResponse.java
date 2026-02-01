package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    UUID channelId,
    UserSummaryResponse author,
    String content,
    List<BinaryContentResponse> attachments,
    Instant createdAt,
    Instant updatedAt
) {

}
