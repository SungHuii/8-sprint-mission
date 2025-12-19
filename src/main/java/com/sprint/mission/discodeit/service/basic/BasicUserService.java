package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
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
        // 寃利?濡쒖쭅 硫붿꽌??
        validateCreateRequest(request);

        // 以묐났 寃??
        if (userRepository.findByNickname(request.nickname()).isPresent()) {
            throw new IllegalArgumentException("?대? 議댁옱?섎뒗 nickname ?낅땲??");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("?대? 議댁옱?섎뒗 email ?낅땲??");
        }

        User user = new User(
                request.name(),
                request.nickname(),
                request.phoneNumber(),
                request.password(),
                request.email()
        );

        // ?꾨줈???대?吏媛 ?덈뒗 寃쎌슦 ?????profileId ?ㅼ젙
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
            throw new IllegalArgumentException("userId???꾩닔?낅땲??");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("?대떦 ?좎?瑜?李얠쓣 ???놁뒿?덈떎."));

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("UserStatus媛 議댁옱?섏? ?딆뒿?덈떎. userId=" + userId));

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
                        (a, b) -> a // ?뱀떆 以묐났?대㈃ 泥ル쾲吏??좎?(?뺤긽 ?곗씠?곕㈃ 以묐났 ?놁뼱????
                ));

        Instant now = Instant.now();

        return users.stream()
                .map(user -> {
                    UserStatus status = statusMap.get(user.getId());
                    if (status == null) {
                        throw new IllegalStateException("UserStatus媛 議댁옱?섏? ?딆뒿?덈떎. userId=" + user.getId());
                    }
                    return toUserResponse(user, status.isOnline(now));
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        validateUpdateRequest(request);

        // 1. ?낅뜲?댄듃 ????좎? 議고쉶 (?놁쑝硫??덉쇅 泥섎━)
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("?대떦 ?좎?媛 議댁옱?섏? ?딆뒿?덈떎. userId=" + request.userId()));

        // 2. 遺遺??섏젙 - null???꾨땶 媛믩쭔 ?낅뜲?댄듃
        if (request.name() != null) {
            user.updateName(request.name());
        }
        if (request.nickname() != null) {
            // nickname 蹂寃쎌씠硫?以묐났 寃???꾩슂(蹂몄씤 ?쒖쇅)
            userRepository.findByNickname(request.nickname())
                    .filter(found -> !found.getId().equals(user.getId())) // ?좎? ?됰꽕?꾧낵 媛숈? ?됰꽕?꾩씠 ?덈뒗吏 ?뺤씤
                    .ifPresent(found -> {
                        throw new IllegalArgumentException("?대? 議댁옱?섎뒗 nickname ?낅땲??");
                    });
            user.updateNickname(request.nickname());
        }
        if (request.phoneNumber() != null) {
            user.updatePhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            // email 蹂寃쎌씠硫?以묐났 寃???꾩슂(蹂몄씤 ?쒖쇅)
            userRepository.findByEmail(request.email())
                    .filter(found -> !found.getId().equals(user.getId())) // ?좎? ?대찓?쇨낵 媛숈? ?대찓?쇱씠 ?덈뒗吏 ?뺤씤
                    .ifPresent(found -> {
                        throw new IllegalArgumentException("?대? 議댁옱?섎뒗 email ?낅땲??");
                    });
            user.updateEmail(request.email());
        }
        if (request.password() != null) {
            user.updatePassword(request.password());
        }

        // 3. ?꾨줈???대?吏 援먯껜 - ?덈줈???대?吏媛 ?덉쑝硫?湲곗〈 ??젣 ???덈줈 ???
        if (request.newProfile() != null) {
            // 湲곗〈 ?꾨줈???대?吏 ??젣
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

        // 4. ???
        User updated = userRepository.updateUser(user);

        // 5. online ?곹깭 ?ы븿?댁꽌 由ы꽩
        UserStatus status = userStatusRepository.findByUserId(updated.getId())
                .orElseThrow(() -> new IllegalStateException("UserStatus媛 議댁옱?섏? ?딆뒿?덈떎. userId=" + updated.getId()));

        boolean online = status.isOnline(Instant.now());
        return toUserResponse(updated, online);
    }

    @Override
    public void deleteById(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId???꾩닔?낅땲??");
        }

        // 1. ?좎? 議고쉶 (?놁쑝硫??덉쇅)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("?대떦 ?좎?媛 議댁옱?섏? ?딆뒿?덈떎. userId=" + userId));

        // 2. ?꾨줈?꾩씠 議댁옱??????젣
        UUID profileId = user.getProfileId();
        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 3. UserStatus ??젣
        userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("UserStatus媛 議댁옱?섏? ?딆뒿?덈떎. userId=" + userId));
        userStatusRepository.deleteByUserId(userId);

        // 4. User ??젣
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
            throw new IllegalArgumentException("?붿껌??null?낅땲??");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("name? ?꾩닔?낅땲??");
        }
        if (request.nickname() == null || request.nickname().isBlank()) {
            throw new IllegalArgumentException("nickname? ?꾩닔?낅땲??");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isBlank()) {
            throw new IllegalArgumentException("phoneNumber???꾩닔?낅땲??");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("password???꾩닔?낅땲??");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("email? ?꾩닔?낅땲??");
        }
    }

    private void validateUpdateRequest(UserUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("?붿껌??null?낅땲??");
        }
        if (request.userId() == null) {
            throw new IllegalArgumentException("userId???꾩닔?낅땲??");
        }

        boolean hasAnyUpdate =
                request.name() != null ||
                request.nickname() != null ||
                request.phoneNumber() != null ||
                request.password() != null ||
                request.email() != null ||
                request.newProfile() != null;

        if (!hasAnyUpdate) {
            throw new IllegalArgumentException("?섏젙??媛믪씠 ?놁뒿?덈떎.");
        }
    }
}


    /*
    Spring Boot ?댁쟾 踰꾩쟾 肄붾뱶??
    @Override
    public User save(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            System.out.println("?대쫫??鍮꾩뼱?덉뒿?덈떎.");
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
                        .orElseThrow(() -> new IllegalArgumentException("?대떦 ?좎?媛 議댁옱?섏? ?딆뒿?덈떎."));
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
                .orElseThrow(() -> new IllegalArgumentException("?대떦 ?좎?瑜?李얠쓣 ???놁뒿?덈떎."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }*/
