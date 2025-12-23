package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    /*
     * 직렬화 UID
     * 5분 상수 표기
     * 고유아이디
     * 생성시간
     * 수정시간
     * 유저 참조
     * 마지막으로 활동한 시간
     * */

    @Serial
    private static final long serialVersionUID = 1L;
    private static final long ONLINE_THRESHOLD_SECONDS = 300;
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.userId = userId;
        this.lastActiveAt = lastActiveAt != null
                ? lastActiveAt
                : Instant.now();
    }

    public void updateLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        renewUpdatedAt();
    }

    public boolean isOnline(Instant now) {
        return !lastActiveAt.isBefore(now.minusSeconds(ONLINE_THRESHOLD_SECONDS));
    }

    private void renewUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
