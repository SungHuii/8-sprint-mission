package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  private String username;
  private String email;
  private String password;
  private BinaryContent profile;

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public void updateUsername(String username) {
    this.username = username;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public void updateProfile(BinaryContent profile) {
    this.profile = profile;
  }
}
