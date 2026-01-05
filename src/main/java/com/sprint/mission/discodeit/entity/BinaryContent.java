package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("bytes") // JSON으로 변환될 때 필드 이름을 "bytes"로 지정
  private final byte[] data;
  
  private final String contentType;
  @JsonProperty("fileName") // JSON으로 변환될 때 필드 이름을 "fileName"으로 지정
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
