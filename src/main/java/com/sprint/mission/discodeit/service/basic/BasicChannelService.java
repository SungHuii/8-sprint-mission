package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public ChannelResponse createPublic(PublicChannelCreateRequest request) {
    validatePublicCreateRequest(request);

    Channel channel = Channel.ofPublic(request.name(), request.description());
    Channel saved = channelRepository.save(channel);

    return toChannelResponse(saved);
  }

  @Override
  @Transactional
  public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
    validatePrivateCreateRequest(request);

    Channel channel = Channel.ofPrivate();
    Channel saved = channelRepository.save(channel);

    // 참여자별 ReadStatus 생성
    for (UUID userId : request.participantIds()) {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. userId=" + userId));

      readStatusRepository.save(new ReadStatus(user, saved, saved.getCreatedAt()));
    }

    return toChannelResponse(saved);
  }

  @Override
  public ChannelResponse findById(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId는 필수입니다.");
    }

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 채널이 존재하지 않습니다. channelId=" + channelId));

    return toChannelResponse(channel);
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId는 필수입니다.");
    }

    // 1. PUBLIC 채널 조회
    List<Channel> publicChannels = channelRepository.findAll().stream()
        .filter(c -> c.getType() == ChannelType.PUBLIC)
        .toList();

    // 2. 참여 중인 PRIVATE 채널 조회
    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
    List<UUID> privateChannelIds = readStatuses.stream()
        .map(rs -> rs.getChannel().getId())
        .toList();

    List<Channel> privateChannels = channelRepository.findAllById(privateChannelIds);

    // 3. 합치기 (중복 제거)
    Set<Channel> allChannels = new HashSet<>();
    allChannels.addAll(publicChannels);
    allChannels.addAll(privateChannels);

    return allChannels.stream()
        .map(this::toChannelResponse)
        .toList();
  }

  @Override
  public List<ChannelResponse> findAll() {
    return channelRepository.findAll().stream()
        .map(this::toChannelResponse)
        .toList();
  }

  @Override
  @Transactional
  public ChannelResponse update(UUID channelId, ChannelUpdateRequest request) {
    validateUpdateRequest(channelId, request);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 채널이 존재하지 않습니다. channelId=" + channelId));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalStateException("비공개 채널은 수정할 수 없습니다.");
    }

    if (request.newName() != null) {
      channel.updateName(request.newName());
    }
    if (request.newDescription() != null) {
      channel.updateDescription(request.newDescription());
    }

    // JPA 변경 감지(Dirty Checking)로 인해 별도의 save 호출 불필요
    return toChannelResponse(channel);
  }

  @Override
  @Transactional
  public void deleteById(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId는 필수입니다.");
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);

    channelRepository.deleteById(channelId);
  }

  private ChannelResponse toChannelResponse(Channel channel) {
    Instant lastMessageAt = findLastMessageAt(channel.getId());
    List<UserSummaryResponse> participants = findParticipants(channel.getId());

    return channelMapper.toChannelResponse(channel, participants, lastMessageAt);
  }

  private Instant findLastMessageAt(UUID channelId) {
    List<Message> messages = messageRepository.findAllByChannelId(channelId);
    return messages.stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }

  private List<UserSummaryResponse> findParticipants(UUID channelId) {
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
    Instant now = Instant.now();

    return readStatuses.stream()
        .map(ReadStatus::getUser)
        .map(user -> {
          UserStatus status = userStatusRepository.findByUserId(user.getId()).orElse(null);
          boolean isOnline = status != null && status.isOnline(now);
          return userMapper.toUserSummaryResponse(user, isOnline);
        })
        .toList();
  }

  private void validatePublicCreateRequest(PublicChannelCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.name() == null || request.name().isBlank()) {
      throw new IllegalArgumentException("name은 필수입니다.");
    }
  }

  private void validatePrivateCreateRequest(PrivateChannelCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.participantIds() == null || request.participantIds().isEmpty()) {
      throw new IllegalArgumentException("participantIds는 필수이며 비어 있을 수 없습니다.");
    }
  }

  private void validateUpdateRequest(UUID channelId, ChannelUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (channelId == null) {
      throw new IllegalArgumentException("channelId는 필수입니다.");
    }
    if (request.newName() == null && request.newDescription() == null) {
      throw new IllegalArgumentException("수정할 값이 없습니다.");
    }
  }
}
