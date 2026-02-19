package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
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

// Mockito 확장
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 기능 테스트")
class UserServiceTest {

  @InjectMocks // mock 객체를 주입받을 테스트 대상
  private BasicUserService userService;

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserMapper userMapper;

  @Test
  @DisplayName("회원가입 성공")
  void create_Success() {
    // given
    UserCreateRequest request = new UserCreateRequest("test@test.com", "testuser", "testpassword",
        null);
    User user = new User("testuser", "test@test.com", "testpassword");
    UserResponse expectedResponse = new UserResponse(UUID.randomUUID(), "testuser", "test@test.com",
        null, true);

    // BDD 적용
    given(userRepository.findByUsername(anyString())).willReturn(Optional.empty()); // 중복 없음
    given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());    // 중복 없음
    given(userRepository.save(any(User.class))).willReturn(user);                         // 저장 성공
    given(userMapper.toUserResponse(any(User.class), any(Boolean.class))).willReturn(
        expectedResponse); // 매핑

    // when
    UserResponse response = userService.create(request);

    // then
    assertThat(response.username()).isEqualTo("testuser");
    verify(userRepository).save(any(User.class)); // save가 호출됐는지 확인
  }

  @Test
  @DisplayName("회원가입 실패 - 중복 사용자명")
  void create_Fail_DuplicateUsername() {
    // given
    UserCreateRequest request = new UserCreateRequest("test@test.com", "duplicateUser", "password",
        null);

    // 이미 존재하는 유저가 있음
    given(userRepository.findByUsername("duplicateUser")).willReturn(
        Optional.of(new User("duplicateUser", "test@test.com", "password")));

    // when & then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(UserException.class) // 예외 발생 확인
        .extracting(e -> ((UserException) e).getErrorCode())
        .isEqualTo(UserErrorCode.DUPLICATE_USERNAME); // 에러코드 확인
  }

  @Test
  @DisplayName("유저 정보 수정 성공")
  void update_Success() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUsername", null, null, null);

    // 기존 유저 생성
    User existingUser = new User("oldUsername", "test@test.com", "password");
    // 수정된 유저
    User updatedUser = new User("newUsername", "test@test.com", "password");

    UserResponse expectedResponse = new UserResponse(userId, "newUsername", "test@test.com", null,
        true);

    // BDD 적용
    given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.findByUsername("newUsername")).willReturn(Optional.empty()); // 중복 없음
    given(userRepository.save(any(User.class))).willReturn(updatedUser); // 저장 성공
    given(userStatusRepository.findByUserId(any())).willReturn(
        Optional.of(new UserStatus(updatedUser, Instant.now())));
    given(userMapper.toUserResponse(any(User.class), any(Boolean.class))).willReturn(
        expectedResponse); // 매핑

    // when
    UserResponse response = userService.update(userId, request);

    // then
    assertThat(response.username()).isEqualTo("newUsername");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 존재하지 않는 유저")
  void update_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUsername", null, null, null);

    // 유저를 찾을 수 없음
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request))
        .isInstanceOf(UserException.class)
        .extracting(e -> ((UserException) e).getErrorCode())
        .isEqualTo(UserErrorCode.USER_NOT_FOUND);
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_Success() {
    // given
    UUID userId = UUID.randomUUID();

    // when
    userService.deleteById(userId);

    // then
    verify(userRepository).deleteById(userId);
  }
}
