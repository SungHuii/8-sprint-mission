package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatusResponse create(UserStatusCreateRequest request) {
    validateCreateRequest(request);

    userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + request.userId()));

    userStatusRepository.findByUserId(request.userId())
        .ifPresent(status -> {
          throw new IllegalArgumentException(
              "이미 유저 상태가 존재합니다. userId=" + request.userId());
        });

    Instant lastActiveAt = request.lastActiveAt() != null ? request.lastActiveAt() : Instant.now();
    UserStatus saved = userStatusRepository.save(new UserStatus(request.userId(), lastActiveAt));

    return toUserStatusResponse(saved);
  }

  @Override
  public UserStatusResponse findById(UUID userStatusId) {
    if (userStatusId == null) {
      throw new IllegalArgumentException("userStatusId는 필수입니다.");
    }

    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userStatusId=" + userStatusId));

    return toUserStatusResponse(status);
  }

  @Override
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(this::toUserStatusResponse)
        .toList();
  }

  @Override
  public UserStatusResponse update(UserStatusUpdateRequest request) {
    validateUpdateRequest(request);

    UserStatus status = userStatusRepository.findById(request.userStatusId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userStatusId=" + request.userStatusId()));

    status.updateLastActiveAt(request.lastActiveAt());
    UserStatus updated = userStatusRepository.save(status);

    return toUserStatusResponse(updated);
  }

  @Override
  public UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request) {
    validateUpdateByUserIdRequest(request);

    UserStatus status = userStatusRepository.findByUserId(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userId=" + request.userId()));

    status.updateLastActiveAt(request.lastActiveAt());
    UserStatus updated = userStatusRepository.save(status);

    return toUserStatusResponse(updated);
  }

  @Override
  public void deleteById(UUID userStatusId) {
    if (userStatusId == null) {
      throw new IllegalArgumentException("userStatusId는 필수입니다.");
    }

    userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저 상태가 존재하지 않습니다. userStatusId=" + userStatusId));

    userStatusRepository.deleteById(userStatusId);
  }

  private UserStatusResponse toUserStatusResponse(UserStatus status) {
    return new UserStatusResponse(
        status.getId(),
        status.getUserId(),
        status.getLastActiveAt(),
        status.getCreatedAt(),
        status.getUpdatedAt()
    );
  }

  private void validateCreateRequest(UserStatusCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.userId() == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }
  }

  private void validateUpdateRequest(UserStatusUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.userStatusId() == null) {
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

