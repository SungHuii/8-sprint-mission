package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserResponse userResponse;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(userResponse.role().getAuthority()));
  }

  @Override
  public String getUsername() {
    return userResponse.username();
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
