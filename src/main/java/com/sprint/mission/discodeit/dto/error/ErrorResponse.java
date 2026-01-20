package com.sprint.mission.discodeit.dto.error;

import java.time.Instant;

public record ErrorResponse(
    String code,
    String message,
    Instant timestamp
) {

}
