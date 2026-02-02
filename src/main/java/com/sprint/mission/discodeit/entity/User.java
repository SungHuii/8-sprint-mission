package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus userStatus;

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
