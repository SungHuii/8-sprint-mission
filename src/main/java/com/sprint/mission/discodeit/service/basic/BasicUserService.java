package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
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
        // 검증 로직 메서드
        validateCreateRequest(request);

        // 중복 검사
        if (userRepository.findByNickname(request.nickname()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 nickname 입니다.");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 email 입니다.");
        }

        User user = new User(
                request.name(),
                request.nickname(),
                request.phoneNumber(),
                request.password(),
                request.email()
        );

        // 프로필 이미지가 있는 경우 저장 후 profileId 설정
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
                .orElseThrow(() -> new IllegalStateException("UserStatus가 존재하지 않습니다. userId=" + userId));

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
                        (a, b) -> a // 혹시 중복이면 첫번째 유지(정상 데이터면 중복 없어야 함)
                ));

        Instant now = Instant.now();

        return users.stream()
                .map(user -> {
                    UserStatus status = statusMap.get(user.getId());
                    if (status == null) {
                        throw new IllegalStateException("UserStatus가 존재하지 않습니다. userId=" + user.getId());
                    }
                    return toUserResponse(user, status.isOnline(now));
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        validateUpdateRequest(request);

        // 1. 업데이트 대상 유저 조회 (없으면 예외 처리)
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. userId=" + request.userId()));

        // 2. 부분 수정 - null이 아닌 값만 업데이트
        if (request.name() != null) {
            user.updateName(request.name());
        }
        if (request.nickname() != null) {
            // nickname 변경이면 중복 검사 필요(본인 제외)
            userRepository.findByNickname(request.nickname())
                    .filter(found -> !found.getId().equals(user.getId())) // 유저 닉네임과 같은 닉네임이 있는지 확인
                    .ifPresent(found -> {
                        throw new IllegalArgumentException("이미 존재하는 nickname 입니다.");
                    });
            user.updateNickname(request.nickname());
        }
        if (request.phoneNumber() != null) {
            user.updatePhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            // email 변경이면 중복 검사 필요(본인 제외)
            userRepository.findByEmail(request.email())
                    .filter(found -> !found.getId().equals(user.getId())) // 유저 이메일과 같은 이메일이 있는지 확인
                    .ifPresent(found -> {
                        throw new IllegalArgumentException("이미 존재하는 email 입니다.");
                    });
            user.updateEmail(request.email());
        }
        if (request.password() != null) {
            user.updatePassword(request.password());
        }

        // 3. 프로필 이미지 교체 - 새로운 이미지가 있으면 기존 삭제 후 새로 저장
        if (request.newProfile() != null) {
            // 기존 프로필 이미지 삭제
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

        // 4. 저장
        User updated = userRepository.updateUser(user);

        // 5. online 상태 포함해서 리턴
        UserStatus status = userStatusRepository.findByUserId(updated.getId())
                .orElseThrow(() -> new IllegalStateException("UserStatus가 존재하지 않습니다. userId=" + updated.getId()));

        boolean online = status.isOnline(Instant.now());
        return toUserResponse(updated, online);
    }

    @Override
    public void deleteById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        // 1. 유저 조회 (없으면 예외)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. userId=" + userId));

        // 2. 프로필이 존재할 때 삭제
        UUID profileId = user.getProfileId();
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 3. UserStatus 삭제
        userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("UserStatus가 존재하지 않습니다. userId=" + userId));
        userStatusRepository.deleteByUserId(userId);

        // 4. User 삭제
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

    private void validateCreateRequest(UserCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }
        if (request.nickname() == null || request.nickname().isBlank()) {
            throw new IllegalArgumentException("nickname은 필수입니다.");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isBlank()) {
            throw new IllegalArgumentException("phoneNumber는 필수입니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("password는 필수입니다.");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("email은 필수입니다.");
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


    /*
    Spring Boot 이전 버전 코드들
    @Override
    public User save(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            System.out.println("이름이 비어있습니다.");
            return null;
        }
        return userRepository.save(user);
    }

    @Override
    public User saveUser(String name, String nickname, String phoneNumber, String password, String email) {
        User user = new User(name, nickname, phoneNumber, password, email);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User checkExisted = userRepository.findById(user.getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
        checkExisted.updateName(user.getName());
        checkExisted.updateNickname(user.getNickname());
        checkExisted.updatePhoneNumber(user.getPhoneNumber());
        checkExisted.updateEmail(user.getEmail());
        checkExisted.updateProfileId(user.getProfileId());
        checkExisted.updatePassword(user.getPassword());

        return userRepository.updateUser(checkExisted);
    }

    @Override
    public void deleteById(UUID userid) {
        userRepository.deleteById(userid);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }*/