package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponse createPublic(PublicChannelCreateRequest request) {
        // 요청 검증
        validatePublicCreateRequest(request);

        // 채널 생성 및 저장
        Channel channel = new Channel(request.name(), request.description());
        Channel saved = channelRepository.save(channel);

        return toChannelResponse(saved, null);
    }

    @Override
    public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        // 요청 검증
        validatePrivateCreateRequest(request);

        // 채널 생성 및 저장
        Channel channel = new Channel(request.participantIds());
        Channel saved = channelRepository.save(channel);

        // 참여자별 ReadStatus 생성
        Instant now = Instant.now();
        for (UUID userId : saved.getParticipantIds()) {
            readStatusRepository.save(new ReadStatus(userId, saved.getId(), now));
        }

        return toChannelResponse(saved, null);
    }

    @Override
    public ChannelResponse findById(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 필수입니다.");
        }

        // 채널 조회
        Channel channel = channelRepository.findById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다. channelId=" + channelId);
        }

        Instant lastMessageAt = findLastMessageAt(channelId);
        return toChannelResponse(channel, lastMessageAt);
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        List<Channel> channels = channelRepository.findAll();

        return channels.stream()
                .filter(channel -> channel.getChType() == ChannelType.PUBLIC
                        || (channel.getParticipantIds() != null
                        && channel.getParticipantIds().contains(userId)))
                .map(channel -> toChannelResponse(channel, findLastMessageAt(channel.getId())))
                .toList();
    }

    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        // 요청 검증
        validateUpdateRequest(request);

        Channel channel = channelRepository.findById(request.channelId());
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다. channelId=" + request.channelId());
        }

        if (channel.getChType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // 부분 수정
        if (request.name() != null) {
            channel.updateChName(request.name());
        }
        if (request.description() != null) {
            channel.updateChDescription(request.description());
        }

        Channel updated = channelRepository.updateChannel(channel);
        Instant lastMessageAt = findLastMessageAt(updated.getId());

        return toChannelResponse(updated, lastMessageAt);
    }

    @Override
    public void deleteById(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 필수입니다.");
        }

        // 관련 데이터 먼저 삭제
        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);

        boolean deleted = channelRepository.deleteChannel(channelId);
        if (!deleted) {
            throw new IllegalArgumentException("채널 삭제 실패(존재하지 않을 수 있음). channelId=" + channelId);
        }
    }

    private ChannelResponse toChannelResponse(Channel channel, Instant lastMessageAt) {
        return new ChannelResponse(
                channel.getId(),
                channel.getChType(),
                channel.getChName(),
                channel.getChDescription(),
                lastMessageAt,
                channel.getParticipantIds()
        );
    }

    private Instant findLastMessageAt(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        return messages.stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
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

    private void validateUpdateRequest(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.channelId() == null) {
            throw new IllegalArgumentException("channelId는 필수입니다.");
        }
        if (request.name() == null && request.description() == null) {
            throw new IllegalArgumentException("수정할 값이 없습니다.");
        }
    }

    /*
    Spring Boot 이전 버전 코드

    @Override
    public Channel save(Channel channel) {
        if (channel.getChName() == null || channel.getChName().isEmpty()) {
            System.out.println("채널명이 비어있습니다.");
            return null;
        }
        return channelRepository.save(channel);
    }

    @Override
    public Channel saveChannel(String name, String description) {
        return new Channel(name, description);
    }

    @Override
    public Channel updateChannel(Channel channel) {
        Channel checkExisted = channelRepository.findById(channel.getId());

        if (checkExisted == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
        checkExisted.updateChName(channel.getChName());
        checkExisted.updateChDescription(channel.getChDescription());

        return channelRepository.updateChannel(channel);
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        return channelRepository.deleteChannel(channelId);
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }*/
}

