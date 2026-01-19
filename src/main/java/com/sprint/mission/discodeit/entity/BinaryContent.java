package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
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
public class BinaryContent extends BaseEntity {

  @Column(name = "bytes", nullable = false)
  private byte[] data;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "file_name", nullable = false)
  private String originalName;

  @Column(nullable = false)
  private long size;

  public BinaryContent(byte[] data, String contentType, String originalName) {
    this.data = data;
    this.contentType = contentType;
    this.originalName = originalName;
    this.size = data != null
        ? data.length
        : 0;
  }
}
