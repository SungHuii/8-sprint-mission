package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusResponse create(UserStatusCreateRequest request) {
    validateCreateRequest(request);

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + request.userId()));

    userStatusRepository.findByUserId(request.userId())
        .ifPresent(status -> {
          throw new IllegalArgumentException(
              "이미 유저 상태가 존재합니다. userId=" + request.userId());
        });

    Instant lastActiveAt = request.lastActiveAt() != null ? request.lastActiveAt() : Instant.now();
    UserStatus saved = userStatusRepository.save(new UserStatus(user, lastActiveAt));

    return userStatusMapper.toUserStatusResponse(saved);
  }

  @Override
  public UserStatusResponse findById(UUID userStatusId) {
    if (userStatusId == null) {
      throw new IllegalArgumentException("userStatusId는 필수입니다.");
    }

    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userStatusId=" + userStatusId));

    return userStatusMapper.toUserStatusResponse(status);
  }

  @Override
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toUserStatusResponse)
        .toList();
  }

  @Override
  @Transactional
  public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
    validateUpdateRequest(userStatusId, request);

    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userStatusId=" + userStatusId));

    status.updateLastActiveAt(request.lastActiveAt());
    // JPA 변경 감지(Dirty Checking)로 인해 별도의 save 호출 불필요

    return userStatusMapper.toUserStatusResponse(status);
  }

  @Override
  @Transactional
  public UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request) {
    validateUpdateByUserIdRequest(request);

    UserStatus status = userStatusRepository.findByUserId(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userId=" + request.userId()));

    status.updateLastActiveAt(request.lastActiveAt());
    // JPA 변경 감지(Dirty Checking)로 인해 별도의 save 호출 불필요

    return userStatusMapper.toUserStatusResponse(status);
  }

  @Override
  @Transactional
  public void deleteById(UUID userStatusId) {
    if (userStatusId == null) {
      throw new IllegalArgumentException("userStatusId는 필수입니다.");
    }

    userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userStatusId=" + userStatusId));

    userStatusRepository.deleteById(userStatusId);
  }

  private void validateCreateRequest(UserStatusCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.userId() == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }
  }

  private void validateUpdateRequest(UUID userStatusId, UserStatusUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (userStatusId == null) {
      throw new IllegalArgumentException("userStatusId는 필수입니다.");
    }
    if (request.lastActiveAt() == null) {
      throw new IllegalArgumentException("lastActiveAt은 필수입니다.");
    }
  }

  private void validateUpdateByUserIdRequest(UserStatusUpdateByUserIdRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.userId() == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }
    if (request.lastActiveAt() == null) {
      throw new IllegalArgumentException("lastActiveAt은 필수입니다.");
    }
  }
}
