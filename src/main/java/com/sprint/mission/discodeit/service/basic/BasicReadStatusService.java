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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusResponse create(ReadStatusCreateRequest request) {
    validateCreateRequest(request);

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + request.userId()));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 채널이 존재하지 않습니다. channelId=" + request.channelId()));

    if (readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 읽음 상태입니다.");
    }

    ReadStatus readStatus = new ReadStatus(
        user,
        channel,
        request.lastReadAt() != null ? request.lastReadAt() : Instant.now()
    );
    ReadStatus saved = readStatusRepository.save(readStatus);

    return readStatusMapper.toReadStatusResponse(saved);
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
  @Transactional
  public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
    validateUpdateRequest(readStatusId, request);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 읽음 상태가 존재하지 않습니다. readStatusId=" + readStatusId));

    readStatus.updateLastReadAt(request.newLastReadAt());

    return readStatusMapper.toReadStatusResponse(readStatus);
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
