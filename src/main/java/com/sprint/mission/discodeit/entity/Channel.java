package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  private String name;
  private String description;
  private ChannelType type;

  // PUBLIC 채널 생성
  private Channel(String name, String description) {
    this.type = ChannelType.PUBLIC;
    this.name = name;
    this.description = description;
  }

  // PRIVATE 채널 생성
  private Channel(ChannelType type) {
    if (type != ChannelType.PRIVATE) {
      throw new IllegalArgumentException("이 생성자는 PRIVATE 채널 전용입니다.");
    }
    this.type = ChannelType.PRIVATE;
    this.name = null;
    this.description = null;
  }

  public static Channel ofPublic(String name, String description) {
    return new Channel(name, description);
  }

  public static Channel ofPrivate() {
    return new Channel(ChannelType.PRIVATE);
  }

  public void updateName(String name) {
    validateChannelType();
    this.name = name;
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  private void validateChannelType() {
    if (this.type == ChannelType.PRIVATE) {
      throw new IllegalStateException("비공개 채널은 수정할 수 없습니다.");
    }
  }
}
