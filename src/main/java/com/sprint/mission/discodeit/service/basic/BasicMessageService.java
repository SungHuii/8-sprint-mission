package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        validateCreateRequest(request);

        userRepository.findById(request.authorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저가 존재하지 않습니다. userId=" + request.authorId()));

        if (channelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다. channelId=" + request.channelId());
        }

        List<UUID> attachmentIds = new ArrayList<>();
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
                attachmentIds.add(saved.getId());
            }
        }

        Message message = new Message(
                request.authorId(),
                request.channelId(),
                request.content(),
                attachmentIds
        );
        Message saved = messageRepository.save(message);
        if (saved == null) {
            throw new IllegalStateException("메시지 저장에 실패했습니다.");
        }

        return toMessageResponse(saved);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 필수입니다.");
        }

        if (channelRepository.findById(channelId) == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다. channelId=" + channelId);
        }

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        validateUpdateRequest(request);

        Message message = messageRepository.findById(request.messageId());
        if (message == null) {
            throw new IllegalArgumentException("해당 메시지가 존재하지 않습니다. messageId=" + request.messageId());
        }

        message.updateMessage(request.content());
        Message updated = messageRepository.updateMessage(message);
        if (updated == null) {
            throw new IllegalStateException("메시지 수정에 실패했습니다. messageId=" + request.messageId());
        }

        return toMessageResponse(updated);
    }

    @Override
    public void deleteById(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId는 필수입니다.");
        }

        Message message = messageRepository.findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("해당 메시지가 존재하지 않습니다. messageId=" + messageId);
        }

        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds != null) {
            for (UUID attachmentId : attachmentIds) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }

        boolean deleted = messageRepository.deleteMessage(messageId);
        if (!deleted) {
            throw new IllegalArgumentException("메시지 삭제 실패(존재하지 않을 수 있음). messageId=" + messageId);
        }
    }

    /*@Deprecated
    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }

        userRepository.findById(message.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저가 존재하지 않습니다. userId=" + message.getAuthorId()));

        if (channelRepository.findById(message.getChannelId()) == null) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다. channelId=" + message.getChannelId());
        }

        if (message.getMessageContent() == null || message.getMessageContent().isBlank()) {
            throw new IllegalArgumentException("messageContent는 필수입니다.");
        }

        return messageRepository.save(message);
    }

    @Deprecated
    @Override
    public Message saveMessage(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
        return new Message(authorId, channelId, content, attachmentIds);
    }

    @Deprecated
    @Override
    public Message updateMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }

        Message existing = messageRepository.findById(message.getId());
        if (existing == null) {
            throw new IllegalArgumentException("해당 메시지가 존재하지 않습니다. messageId=" + message.getId());
        }

        existing.updateMessage(message.getMessageContent());
        return messageRepository.updateMessage(existing);
    }

    @Deprecated
    @Override
    public boolean deleteMessage(UUID messageId) {
        return messageRepository.deleteMessage(messageId);
    }

    @Deprecated
    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Deprecated
    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }*/

    private MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getMessageContent(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
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

    private void validateUpdateRequest(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 null입니다.");
        }
        if (request.messageId() == null) {
            throw new IllegalArgumentException("messageId는 필수입니다.");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("content는 필수입니다.");
        }
    }
}
