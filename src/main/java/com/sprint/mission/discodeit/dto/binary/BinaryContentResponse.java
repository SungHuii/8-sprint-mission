package com.sprint.mission.discodeit.dto.binary;

import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    long size,
    String contentType
) {
}
