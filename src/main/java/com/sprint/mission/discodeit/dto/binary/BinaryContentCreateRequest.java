package com.sprint.mission.discodeit.dto.binary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일명은 필수입니다.")
    String fileName,

    @Positive(message = "파일 크기는 0보다 커야 합니다.")
    long size,

    @NotBlank(message = "Content-Type은 필수입니다.")
    String contentType,

    @NotEmpty(message = "파일 데이터는 비어있을 수 없습니다.")
    byte[] bytes
) {
}
