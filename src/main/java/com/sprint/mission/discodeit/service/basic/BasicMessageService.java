package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;

  @Override
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

        BinaryContent attachment = new BinaryContent(
            attachmentRequest.data(),
            attachmentRequest.contentType(),
            attachmentRequest.originalName()
        );
        BinaryContent saved = binaryContentRepository.save(attachment);
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
  public List<MessageResponse> findAllByChannelId(UUID channelId) {
    if (channelId == null) {
      throw new IllegalArgumentException("channelId는 필수입니다.");
    }

    channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 채널이 존재하지 않습니다. channelId=" + channelId));

    return messageRepository.findAllByChannelId(channelId).stream()
        .map(messageMapper::toMessageResponse)
        .toList();
  }

  @Override
  public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
    validateUpdateRequest(messageId, request);

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 메시지가 존재하지 않습니다. messageId=" + messageId));

    message.updateContent(request.content());
    Message updated = messageRepository.updateMessage(message);

    return messageMapper.toMessageResponse(updated);
  }

  @Override
  public void deleteById(UUID messageId) {
    if (messageId == null) {
      throw new IllegalArgumentException("messageId는 필수입니다.");
    }

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException(
            "해당 메시지가 존재하지 않습니다. messageId=" + messageId));

    List<BinaryContent> attachments = message.getAttachments();
    if (attachments != null) {
      for (BinaryContent attachment : attachments) {
        binaryContentRepository.deleteById(attachment.getId());
      }
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
    if (request.content() == null || request.content().isBlank()) {
      throw new IllegalArgumentException("content는 필수입니다.");
    }
  }
}
