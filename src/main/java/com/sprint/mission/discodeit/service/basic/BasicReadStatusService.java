package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        validateCreateRequest(request);

        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저가 존재하지 않습니다. userId=" + request.userId()));

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 채널이 존재하지 않습니다. channelId=" + request.channelId()));

        readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
                .ifPresent(status -> {
                    throw new IllegalArgumentException("이미 읽음 상태가 존재합니다. userId="
                            + request.userId() + ", channelId=" + request.channelId());
                });

        Instant lastReadAt = request.lastReadAt() != null
                ? request.lastReadAt()
                : Instant.now();

        ReadStatus saved = readStatusRepository.save(
                new ReadStatus(request.userId(), request.channelId(), lastReadAt));

        return toReadStatusResponse(saved);
    }

    @Override
    public ReadStatusResponse findById(UUID readStatusId) {
        if (readStatusId == null) {
            throw new IllegalArgumentException("readStatusId는 필수입니다.");
        }

        ReadStatus status = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 읽음 상태가 존재하지 않습니다. readStatusId=" + readStatusId));

        return toReadStatusResponse(status);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toReadStatusResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        validateUpdateRequest(request);

        ReadStatus status = readStatusRepository.findById(request.readStatusId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 읽음 상태가 존재하지 않습니다. readStatusId=" + request.readStatusId()));

        status.updateLastReadAt(request.lastReadAt());
        ReadStatus updated = readStatusRepository.save(status);

        return toReadStatusResponse(updated);
    }

    @Override
    public void deleteById(UUID readStatusId) {
        if (readStatusId == null) {
            throw new IllegalArgumentException("readStatusId는 필수입니다.");
        }

        readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 읽음 상태가 존재하지 않습니다. readStatusId=" + readStatusId));

        readStatusRepository.deleteById(readStatusId);
    }

    private ReadStatusResponse toReadStatusResponse(ReadStatus status) {
        return new ReadStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getChannelId(),
                status.getLastReadAt(),
                status.getCreatedAt(),
                status.getUpdatedAt()
        );
    }

    private void validateCreateRequest(ReadStatusCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.userId() == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (request.channelId() == null) {
            throw new IllegalArgumentException("channelId는 필수입니다.");
        }
    }

    private void validateUpdateRequest(ReadStatusUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.readStatusId() == null) {
            throw new IllegalArgumentException("readStatusId는 필수입니다.");
        }
        if (request.lastReadAt() == null) {
            throw new IllegalArgumentException("lastReadAt은 필수입니다.");
        }
    }
}

