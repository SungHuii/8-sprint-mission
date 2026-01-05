package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  public UserResponse create(UserCreateRequest request) {
    validateCreateRequest(request);

    if (userRepository.findByNickname(request.username()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
    }
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    User user = new User(
        request.username(), // name
        request.username(), // nickname
        "", // phoneNumber
        request.password(),
        request.email()
    );

    if (request.profile() != null) {
      var profileReq = request.profile();

      BinaryContent profile = new BinaryContent(
          profileReq.data(),
          profileReq.contentType(),
          profileReq.originalName()
      );

      BinaryContent savedProfile = binaryContentRepository.save(profile);
      user.updateProfileId(savedProfile.getId());
    }

    User savedUser = userRepository.save(user);

    Instant now = Instant.now();
    userStatusRepository.save(new UserStatus(savedUser.getId(), now));

    return toUserResponse(savedUser, true);
  }

  @Override
  public UserResponse findById(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalStateException("유저 상태가 존재하지 않습니다. userId=" + userId));

    boolean isOnline = status.isOnline(Instant.now());
    return toUserResponse(user, isOnline);
  }

  @Override
  public List<UserResponse> findAll() {
    List<User> users = userRepository.findAll();
    List<UserStatus> userStatuses = userStatusRepository.findAll();

    var statusMap = userStatuses.stream()
        .collect(Collectors.toMap(
            UserStatus::getUserId,
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
          return toUserResponse(user, status.isOnline(now));
        })
        .toList();
  }

  @Override
  public List<UserDto> findAllUserDtos() {
    List<User> users = userRepository.findAll();
    List<UserStatus> userStatuses = userStatusRepository.findAll();

    var statusMap = userStatuses.stream()
        .collect(Collectors.toMap(
            UserStatus::getUserId,
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
          return toUserDto(user, status.isOnline(now));
        })
        .toList();
  }

  @Override
  public UserResponse update(UserUpdateRequest request) {
    validateUpdateRequest(request);

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + request.userId()));

    if (request.name() != null) {
      user.updateName(request.name());
    }
    if (request.nickname() != null) {
      userRepository.findByNickname(request.nickname())
          .filter(found -> !found.getId().equals(user.getId()))
          .ifPresent(found -> {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
          });
      user.updateNickname(request.nickname());
    }
    if (request.phoneNumber() != null) {
      user.updatePhoneNumber(request.phoneNumber());
    }
    if (request.email() != null) {
      userRepository.findByEmail(request.email())
          .filter(found -> !found.getId().equals(user.getId()))
          .ifPresent(found -> {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
          });
      user.updateEmail(request.email());
    }
    if (request.password() != null) {
      user.updatePassword(request.password());
    }

    if (request.newProfile() != null) {
      UUID oldProfileId = user.getProfileId();
      if (oldProfileId != null) {
        binaryContentRepository.deleteById(oldProfileId);
      }

      var profileReq = request.newProfile();
      BinaryContent newProfile = new BinaryContent(
          profileReq.data(),
          profileReq.contentType(),
          profileReq.originalName()
      );
      BinaryContent saved = binaryContentRepository.save(newProfile);

      user.updateProfileId(saved.getId());
    }

    User updated = userRepository.updateUser(user);

    UserStatus status = userStatusRepository.findByUserId(updated.getId())
        .orElseThrow(() -> new IllegalStateException(
            "유저 상태가 존재하지 않습니다. userId=" + updated.getId()));

    boolean online = status.isOnline(Instant.now());
    return toUserResponse(updated, online);
  }

  @Override
  public void deleteById(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + userId));

    UUID profileId = user.getProfileId();
    if (profileId != null) {
      binaryContentRepository.deleteById(profileId);
    }

    userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalStateException(
            "유저 상태가 존재하지 않습니다. userId=" + userId));
    userStatusRepository.deleteByUserId(userId);

    userRepository.deleteById(userId);
  }

  private UserResponse toUserResponse(User user, boolean isOnline) {
    return new UserResponse(
        user.getId(),
        user.getName(),
        user.getNickname(),
        user.getPhoneNumber(),
        user.getEmail(),
        user.getProfileId(),
        isOnline
    );
  }

  private UserDto toUserDto(User user, boolean isOnline) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getNickname(),
        user.getEmail(),
        user.getProfileId(),
        isOnline
    );
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

  private void validateUpdateRequest(UserUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.userId() == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }

    boolean hasAnyUpdate =
        request.name() != null ||
            request.nickname() != null ||
            request.phoneNumber() != null ||
            request.password() != null ||
            request.email() != null ||
            request.newProfile() != null;

    if (!hasAnyUpdate) {
      throw new IllegalArgumentException("수정할 값이 없습니다.");
    }
  }
}
