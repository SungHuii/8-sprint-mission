package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  private User author;
  private Channel channel;
  private String content;
  private List<BinaryContent> attachments;

  public Message(User author, Channel channel, String content, List<BinaryContent> attachments) {
    this.author = author;
    this.channel = channel;
    this.content = content;
    this.attachments = (attachments != null)
        ? new ArrayList<>(attachments)
        : new ArrayList<>();
  }

  public void updateContent(String content) {
    this.content = content;
  }
}
