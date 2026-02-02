package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
    UUID authorId,
    UUID channelId,
    String content,
    List<BinaryContentCreateRequest> attachments
) {

}
