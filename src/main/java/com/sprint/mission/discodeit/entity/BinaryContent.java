package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA만 접근할 수 있도록 함
public class BinaryContent extends BaseEntity {

  /*
   * 고유아이디 (상속)
   * 생성시간 (상속)
   * byte[] 데이터
   * 타입 (이미지, 파일 등)
   * 원본 파일명
   * 파일 크기
   * */

  @JsonProperty("bytes") // JSON으로 변환될 때 필드 이름을 "bytes"로 지정
  private byte[] data;

  private String contentType;

  @JsonProperty("fileName") // JSON으로 변환될 때 필드 이름을 "fileName"으로 지정
  private String originalName;

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
