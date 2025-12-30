package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

  /*
   * 직렬화 UID
   * 고유아이디
   * 생성시간
   * byte[] 데이터
   * 타입 (이미지, 파일 등)
   * 원본 파일명
   * 파일 크기
   * */

  @Serial
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private final byte[] data;
  private final String contentType;
  private final String originalName;
  private final long size;

  public BinaryContent(byte[] data, String contentType, String originalName) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.data = data;
    this.contentType = contentType;
    this.originalName = originalName;
    this.size = data != null
        ? data.length
        : 0;
  }


}
