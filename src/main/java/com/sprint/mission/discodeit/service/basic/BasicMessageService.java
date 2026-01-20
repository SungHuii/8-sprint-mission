package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentService binaryContentService;
  private final MessageMapper messageMapper;

  @Override
  @Transactional
  public MessageResponse create(MessageCreateRequest request) {
    validateCreateRequest(request);

    User author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 유저가 존재하지 않습니다. userId=" + request.authorId()));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 채널이 존재하지 않습니다. channelId=" + request.channelId()));

    List<BinaryContent> attachments = new ArrayList<>();
    if (request.attachments() != null) {
      for (BinaryContentCreateRequest attachmentRequest : request.attachments()) {
        if (attachmentRequest == null) {
          throw new IllegalArgumentException("attachment 요청이 null입니다.");
        }

        BinaryContent saved = binaryContentService.create(attachmentRequest);
        attachments.add(saved);
      }
    }

    Message message = new Message(
        author,
        channel,
        request.content(),
        attachments
    );
    Message saved = messageRepository.save(message);

    return messageMapper.toMessageResponse(saved);
  }

  @Override
  public PageResponse<MessageResponse> findAllByChannelId(UUID channelId, UUID cursor, Pageable pageable) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId는 필수입니다.");
    }

    channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 채널이 존재하지 않습니다. channelId=" + channelId));

    Slice<Message> messageSlice;
    if (cursor == null) {
      messageSlice = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channelId, pageable);
    } else {
      Message cursorMessage = messageRepository.findById(cursor)
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 커서입니다. cursor=" + cursor));
      messageSlice = messageRepository.findAllByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
          channelId, cursorMessage.getCreatedAt(), pageable);
    }

    List<MessageResponse> content = messageSlice.stream()
        .map(messageMapper::toMessageResponse)
        .toList();

    Object nextCursor = null;
    if (messageSlice.hasNext() && !content.isEmpty()) {
      nextCursor = content.get(content.size() - 1).id();
    }

    return new PageResponse<>(
        content,
        nextCursor,
        messageSlice.getSize(),
        messageSlice.hasNext(),
        null
    );
  }

  @Override
  @Transactional
  public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
    validateUpdateRequest(messageId, request);

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 메시지가 존재하지 않습니다. messageId=" + messageId));

    message.updateContent(request.newContent());

    return messageMapper.toMessageResponse(message);
  }

  @Override
  @Transactional
  public void deleteById(UUID messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("messageId는 필수입니다.");
    }

    messageRepository.deleteById(messageId);
  }

  private void validateCreateRequest(MessageCreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (request.authorId() == null) {
      throw new IllegalArgumentException("authorId는 필수입니다.");
    }
    if (request.channelId() == null) {
      throw new IllegalArgumentException("channelId는 필수입니다.");
    }
    if (request.content() == null || request.content().isBlank()) {
      throw new IllegalArgumentException("content는 필수입니다.");
    }
  }

  private void validateUpdateRequest(UUID messageId, MessageUpdateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청이 null입니다.");
    }
    if (messageId == null) {
      throw new IllegalArgumentException("messageId는 필수입니다.");
    }
    if (request.newContent() == null || request.newContent().isBlank()) {
      throw new IllegalArgumentException("newContent는 필수입니다.");
    }
  }
}
