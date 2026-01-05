package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = RepoProps.PREFIX,
        name = RepoProps.TYPE_NAME,
        havingValue = RepoProps.TYPE_JCF,
        matchIfMissing = true
)
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> data = new ConcurrentHashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return Optional.ofNullable(data.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void deleteById(UUID userStatusId) {
        UserStatus removed = data.remove(userStatusId);
        if (removed == null) {
            throw new NoSuchElementException("해당 유저 상태가 존재하지 않습니다. id=" + userStatusId);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        UserStatus existing = findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 유저 상태가 존재하지 않습니다. userId=" + userId));
        data.remove(existing.getId());
    }
}
