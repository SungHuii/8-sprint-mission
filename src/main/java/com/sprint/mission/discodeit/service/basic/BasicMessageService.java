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
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.enums.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.enums.CommonErrorCode;
import com.sprint.mission.discodeit.exception.enums.MessageErrorCode;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
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
    log.info("메시지 생성 요청: channelId={}, authorId={}", request.channelId(), request.authorId());

    User author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

    List<BinaryContent> attachments = new ArrayList<>();
    if (request.attachments() != null) {
      for (BinaryContentCreateRequest attachmentRequest : request.attachments()) {
        if (attachmentRequest == null) {
          throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "attachment 요청이 null입니다.");
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

    log.info("메시지 생성 완료: messageId={}", saved.getId());
    return messageMapper.toMessageResponse(saved);
  }

  @Override
  public PageResponse<MessageResponse> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
    if (channelId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "channelId는 필수입니다.");
    }
    log.debug("메시지 목록 조회 요청: channelId={}, cursor={}", channelId, cursor);

    channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND));

    Slice<Message> messageSlice;
    if (cursor == null) {
      messageSlice = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channelId, pageable);
    } else {
      messageSlice = messageRepository.findAllByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(
          channelId, cursor, pageable);
    }

    List<MessageResponse> content = messageSlice.stream()
        .map(messageMapper::toMessageResponse)
        .toList();

    Object nextCursor = null;
    if (messageSlice.hasNext() && !content.isEmpty()) {
      nextCursor = content.get(content.size() - 1).createdAt();
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
    log.info("메시지 수정 요청: messageId={}", messageId);

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND));

    message.updateContent(request.newContent());

    log.info("메시지 수정 완료: messageId={}", messageId);
    return messageMapper.toMessageResponse(message);
  }

  @Override
  @Transactional
  public void deleteById(UUID messageId) {
    if (messageId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "messageId는 필수입니다.");
    }
    log.info("메시지 삭제 요청: messageId={}", messageId);

    messageRepository.deleteById(messageId);
  }

  private void validateCreateRequest(MessageCreateRequest request) {
    if (request == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "요청이 null입니다.");
    }
    if (request.authorId() == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "authorId는 필수입니다.");
    }
    if (request.channelId() == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "channelId는 필수입니다.");
    }
    if (request.content() == null || request.content().isBlank()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "content는 필수입니다.");
    }
  }

  private void validateUpdateRequest(UUID messageId, MessageUpdateRequest request) {
    if (request == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "요청이 null입니다.");
    }
    if (messageId == null) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "messageId는 필수입니다.");
    }
    if (request.newContent() == null || request.newContent().isBlank()) {
      throw new DiscodeitException(CommonErrorCode.INVALID_INPUT_VALUE, "newContent는 필수입니다.");
    }
  }
}
