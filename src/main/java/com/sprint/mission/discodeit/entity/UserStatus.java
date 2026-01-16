package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

  private static final long ONLINE_THRESHOLD_SECONDS = 300;

  private User user;
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt != null
        ? lastActiveAt
        : Instant.now();
  }

  public void updateLastActiveAt(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
  }

  public boolean isOnline(Instant now) {
    return !lastActiveAt.isBefore(now.minusSeconds(ONLINE_THRESHOLD_SECONDS));
  }
}
