package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusResponse create(ReadStatusCreateRequest request) {
    validateCreateRequest(request);

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + request.userId()));

    Channel channel = channelRepository.findById(request.channelId())
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
        new ReadStatus(user, channel, lastReadAt));

    return readStatusMapper.toReadStatusResponse(saved);
  }

  @Override
  public ReadStatusResponse findById(UUID readStatusId) {
    if (readStatusId == null) {
      throw new IllegalArgumentException("readStatusId는 필수입니다.");
    }

    ReadStatus status = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 읽음 상태가 존재하지 않습니다. readStatusId=" + readStatusId));

    return readStatusMapper.toReadStatusResponse(status);
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toReadStatusResponse)
        .toList();
  }

  @Override
  public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
    validateUpdateRequest(readStatusId, request);

    ReadStatus status = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 읽음 상태가 존재하지 않습니다. readStatusId=" + readStatusId));

    status.updateLastReadAt(request.newLastReadAt());
    ReadStatus updated = readStatusRepository.save(status);

    return readStatusMapper.toReadStatusResponse(updated);
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

  private void validateUpdateRequest(UUID readStatusId, ReadStatusUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (readStatusId == null) {
      throw new IllegalArgumentException("readStatusId는 필수입니다.");
    }
    if (request.newLastReadAt() == null) {
      throw new IllegalArgumentException("newLastReadAt은 필수입니다.");
    }
  }
}
