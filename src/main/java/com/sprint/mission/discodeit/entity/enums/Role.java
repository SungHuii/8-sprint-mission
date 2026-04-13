package com.sprint.mission.discodeit.entity.enums;

public enum Role {
  USER,
  CHANNEL_MANAGER,
  ADMIN;

  // ROLE_ 접두사 붙여서 반환
  public String getAuthority() {
    return "ROLE_" + this.name();
  }
}
