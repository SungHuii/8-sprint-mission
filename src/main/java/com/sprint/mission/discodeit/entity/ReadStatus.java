package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  private User user;
  private Channel channel;
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt != null
        ? lastReadAt
        : Instant.now();
  }

  public void updateLastReadAt(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
  }
}
