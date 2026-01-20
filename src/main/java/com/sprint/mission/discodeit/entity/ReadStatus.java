package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {

  /*
   * 직렬화 UID
   * 고유아이디
   * 생성시간
   * 수정시간
   * 유저 참조
   * 채널 참조
   * 마지막으로 읽은 시간
   * */

  @Serial
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private final UUID userId;
  private final UUID channelId;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt != null
        ? lastReadAt
        : Instant.now();
  }

  public void updateLastReadAt(Instant lastReadAt) {
    this.lastReadAt = lastReadAt;
    renewUpdatedAt();
  }

  private void renewUpdatedAt() {
    this.updatedAt = Instant.now();
  }
}
