package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(data.get(readStatusId));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null || channelId == null) {
            return Optional.empty();
        }
        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId())
                        && channelId.equals(status.getChannelId()))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            return List.of();
        }
        return data.values().stream()
                .filter(status -> channelId.equals(status.getChannelId()))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) {
            return List.of();
        }
        return data.values().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .toList();
    }

    @Override
    public void deleteById(UUID readStatusId) {
        ReadStatus removed = data.remove(readStatusId);
        if (removed == null) {
            throw new NoSuchElementException("해당 ReadStatus가 존재하지 않습니다. id=" + readStatusId);
        }
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        if (channelId == null) {
            return;
        }
        data.values().removeIf(status -> channelId.equals(status.getChannelId()));
    }
}
