package com.sprint.mission.discodeit.dto.binary;

import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    long size,
    String contentType,
    String url,
    BinaryContentStatus status
) {

}
