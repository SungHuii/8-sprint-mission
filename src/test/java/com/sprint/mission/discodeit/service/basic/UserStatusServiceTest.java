package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserStatusServiceTest {

  @InjectMocks
  private BasicUserStatusService userStatusService;

  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusMapper userStatusMapper;

  @Test
  @DisplayName("유저 상태 생성 성공")
  void create_Success() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    
    User user = new User("user", "email", "pw");
    UserStatus userStatus = new UserStatus(user, Instant.now());
    UserStatusResponse expectedResponse = new UserStatusResponse(UUID.randomUUID(), userId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty()); // 중복 없음
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
    given(userStatusMapper.toUserStatusResponse(any(UserStatus.class))).willReturn(expectedResponse);

    // when
    UserStatusResponse response = userStatusService.create(request);

    // then
    assertThat(response.userId()).isEqualTo(userId);
    verify(userStatusRepository).save(any(UserStatus.class));
  }

  @Test
  @DisplayName("유저 상태 생성 실패 - 존재하지 않는 유저")
  void create_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("해당 유저가 존재하지 않습니다");
  }

  @Test
  @DisplayName("유저 상태 생성 실패 - 이미 존재하는 상태")
  void create_Fail_Duplicate() {
    // given
    UUID userId = UUID.randomUUID();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    User user = new User("user", "email", "pw");
    UserStatus existingStatus = new UserStatus(user, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(existingStatus));

    // when & then
    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 유저 상태가 존재합니다");
  }

  @Test
  @DisplayName("유저 상태 수정 성공")
  void update_Success() {
    // given
    UUID userStatusId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(userStatusId, Instant.now());
    
    User user = new User("user", "email", "pw");
    UserStatus userStatus = new UserStatus(user, Instant.now());
    UserStatusResponse expectedResponse = new UserStatusResponse(userStatusId, user.getId(), Instant.now());

    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toUserStatusResponse(any(UserStatus.class))).willReturn(expectedResponse);

    // when
    UserStatusResponse response = userStatusService.update(userStatusId, request);

    // then
    assertThat(response.id()).isEqualTo(userStatusId);
  }

  @Test
  @DisplayName("유저 상태 수정 실패 - 존재하지 않는 상태 ID")
  void update_Fail_NotFound() {
    // given
    UUID userStatusId = UUID.randomUUID();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(userStatusId, Instant.now());

    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.update(userStatusId, request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("해당 유저 상태가 존재하지 않습니다");
  }

  @Test
  @DisplayName("유저 상태 조회 성공")
  void findById_Success() {
    // given
    UUID userStatusId = UUID.randomUUID();
    User user = new User("user", "email", "pw");
    UserStatus userStatus = new UserStatus(user, Instant.now());
    UserStatusResponse expectedResponse = new UserStatusResponse(userStatusId, user.getId(), Instant.now());

    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toUserStatusResponse(any(UserStatus.class))).willReturn(expectedResponse);

    // when
    UserStatusResponse response = userStatusService.findById(userStatusId);

    // then
    assertThat(response.id()).isEqualTo(userStatusId);
  }

  @Test
  @DisplayName("유저 상태 조회 실패 - 존재하지 않는 상태 ID")
  void findById_Fail_NotFound() {
    // given
    UUID userStatusId = UUID.randomUUID();

    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.findById(userStatusId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("해당 유저 상태가 존재하지 않습니다");
  }
}
