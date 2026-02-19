package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.enums.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.enums.CommonErrorCode;
import com.sprint.mission.discodeit.exception.enums.ReadStatusErrorCode;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
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
    log.info("읽음 상태 생성 요청: userId={}, channelId={}", request.userId(), request.channelId());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

    if (readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .isPresent()) {
      throw new ReadStatusException(ReadStatusErrorCode.DUPLICATE_READ_STATUS);
    }

    ReadStatus readStatus = new ReadStatus(
        user,
        channel,
        request.lastReadAt() != null ? request.lastReadAt() : Instant.now()
    );
    ReadStatus saved = readStatusRepository.save(readStatus);

    log.info("읽음 상태 생성 완료: readStatusId={}", saved.getId());
    return readStatusMapper.toReadStatusResponse(saved);
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    if (userId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "userId는 필수입니다.");
    }
    log.debug("유저별 읽음 상태 목록 조회 요청: userId={}", userId);

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toReadStatusResponse)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
    validateUpdateRequest(readStatusId, request);
    log.info("읽음 상태 수정 요청: readStatusId={}", readStatusId);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusException(ReadStatusErrorCode.READ_STATUS_NOT_FOUND));

    readStatus.updateLastReadAt(request.newLastReadAt());

    log.info("읽음 상태 수정 완료: readStatusId={}", readStatusId);
    return readStatusMapper.toReadStatusResponse(readStatus);
  }

  private void validateCreateRequest(ReadStatusCreateRequest request) {
    if (request == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "요청이 null입니다.");
    }
    if (request.userId() == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "userId는 필수입니다.");
    }
    if (request.channelId() == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "channelId는 필수입니다.");
    }
  }

  private void validateUpdateRequest(UUID readStatusId, ReadStatusUpdateRequest request) {
    if (request == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "요청이 null입니다.");
    }
    if (readStatusId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "readStatusId는 필수입니다.");
    }
    if (request.newLastReadAt() == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "newLastReadAt은 필수입니다.");
    }
  }
}
