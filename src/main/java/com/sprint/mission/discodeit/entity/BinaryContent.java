package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity {

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "file_name", nullable = false)
  private String originalName;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private BinaryContentStatus status;

  public BinaryContent(String contentType, String originalName, long size) {
    this.contentType = contentType;
    this.originalName = originalName;
    this.size = size;
    this.status = BinaryContentStatus.PROCESSING;
  }

  public BinaryContentStatus updateStatus(BinaryContentStatus status) {
    this.status = status;
    return status;
  }
}
