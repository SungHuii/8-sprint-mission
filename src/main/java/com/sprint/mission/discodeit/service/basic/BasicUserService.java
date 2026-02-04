package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserResponse create(UserCreateRequest request) {
    validateCreateRequest(request);
    log.info("회원가입 요청: username={}, email={}", request.username(), request.email());

    if (userRepository.findByUsername(request.username()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
    }
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    User user = new User(
        request.username(),
        request.email(),
        request.password()
    );

    if (request.profile() != null) {
      BinaryContent profile = binaryContentService.create(request.profile());
      user.updateProfile(profile);
    }

    User savedUser = userRepository.save(user);

    Instant now = Instant.now();
    userStatusRepository.save(new UserStatus(savedUser, now));
    log.info("회원가입 완료: userId={}", savedUser.getId());

    return userMapper.toUserResponse(savedUser, true);
  }

  @Override
  public List<UserResponse> findAll() {
    List<User> users = userRepository.findAllWithProfile();
    List<UserStatus> userStatuses = userStatusRepository.findAll();
    log.debug("전체 유저 조회 요청");

    var statusMap = userStatuses.stream()
        .collect(Collectors.toMap(
            s -> s.getUser().getId(),
            s -> s,
            (a, b) -> a
        ));

    Instant now = Instant.now();

    return users.stream()
        .map(user -> {
          UserStatus status = statusMap.get(user.getId());
          if (status == null) {
            throw new IllegalStateException("유저 상태가 존재하지 않습니다. userId=" + user.getId());
          }
          return userMapper.toUserResponse(user, status.isOnline(now));
        })
        .toList();
  }

  @Override
  public List<UserSummaryResponse> findAllUserSummaries() {
    List<User> users = userRepository.findAllWithProfile();
    List<UserStatus> userStatuses = userStatusRepository.findAll();
    log.debug("전체 유저 요약 조회 요청");

    var statusMap = userStatuses.stream()
        .collect(Collectors.toMap(
            s -> s.getUser().getId(),
            s -> s,
            (a, b) -> a
        ));

    Instant now = Instant.now();

    return users.stream()
        .map(user -> {
          UserStatus status = statusMap.get(user.getId());
          if (status == null) {
            throw new IllegalStateException("유저 상태가 존재하지 않습니다. userId=" + user.getId());
          }
          return userMapper.toUserSummaryResponse(user, status.isOnline(now));
        })
        .toList();
  }

  @Override
  @Transactional
  public UserResponse update(UUID userId, UserUpdateRequest request) {
    validateUpdateRequest(userId, request);
    log.info("유저 정보 수정 요청 : userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + userId));

    if (request.newUsername() != null) {
      userRepository.findByUsername(request.newUsername())
          .filter(found -> !found.getId().equals(user.getId()))
          .ifPresent(found -> {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
          });
      user.updateUsername(request.newUsername());
    }
    if (request.newEmail() != null) {
      userRepository.findByEmail(request.newEmail())
          .filter(found -> !found.getId().equals(user.getId()))
          .ifPresent(found -> {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
          });
      user.updateEmail(request.newEmail());
    }
    if (request.newPassword() != null) {
      user.updatePassword(request.newPassword());
    }

    if (request.newProfile() != null) {
      BinaryContent newProfile = binaryContentService.create(request.newProfile());
      user.updateProfile(newProfile);
    }

    User updated = userRepository.save(user);

    UserStatus status = userStatusRepository.findByUserId(updated.getId())
        .orElseThrow(() -> new IllegalStateException(
            "유저 상태가 존재하지 않습니다. userId=" + updated.getId()));

    boolean online = status.isOnline(Instant.now());

    log.info("유저 정보 수정 완료 : userId={}", updated.getId());
    return userMapper.toUserResponse(updated, online);
  }

  @Override
  @Transactional
  public void deleteById(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }
    log.info("유저 삭제 요청 : userId={}", userId);

    userRepository.deleteById(userId);
  }

  private void validateCreateRequest(UserCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.username() == null || request.username().isBlank()) {
      throw new IllegalArgumentException("사용자명은 필수입니다.");
    }
    if (request.password() == null || request.password().isBlank()) {
      throw new IllegalArgumentException("비밀번호는 필수입니다.");
    }
    if (request.email() == null || request.email().isBlank()) {
      throw new IllegalArgumentException("이메일은 필수입니다.");
    }
  }

  private void validateUpdateRequest(UUID userId, UserUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (userId == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }

    boolean hasAnyUpdate =
        request.newUsername() != null ||
            request.newPassword() != null ||
            request.newEmail() != null ||
            request.newProfile() != null;

    if (!hasAnyUpdate) {
      throw new IllegalArgumentException("수정할 값이 없습니다.");
    }
  }
}
