package com.sprint.mission.discodeit.dto.binary;

public record BinaryContentCreateRequest(
    byte[] data,
    String contentType,
    String originalName
) {

}

