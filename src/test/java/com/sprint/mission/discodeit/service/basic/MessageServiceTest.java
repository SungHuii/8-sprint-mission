package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.enums.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.enums.MessageErrorCode;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

  @InjectMocks
  private BasicMessageService messageService;

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private BinaryContentService binaryContentService;
  @Mock
  private MessageMapper messageMapper;

  @Test
  @DisplayName("메시지 생성 성공")
  void create_Success() {
    // given
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId, null);

    User author = new User("user", "email", "pw");
    Channel channel = Channel.ofPublic("ch", "desc");
    Message message = new Message(author, channel, "Hello", Collections.emptyList());
    MessageResponse expectedResponse = new MessageResponse(
        UUID.randomUUID(),
        channelId,
        null,
        "Hello",
        Collections.emptyList(),
        Instant.now(),
        Instant.now()
    );

    given(userRepository.findById(authorId)).willReturn(Optional.of(author));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(messageRepository.save(any(Message.class))).willReturn(message);
    given(messageMapper.toMessageResponse(any(Message.class))).willReturn(expectedResponse);

    // when
    MessageResponse response = messageService.create(request);

    // then
    assertThat(response.content()).isEqualTo("Hello");
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 작성자")
  void create_Fail_UserNotFound() {
    // given
    UUID authorId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId, null);

    given(userRepository.findById(authorId)).willReturn(Optional.empty()); // 유저 없음

    // when & then
    assertThatThrownBy(() -> messageService.create(request))
        .isInstanceOf(UserException.class)
        .extracting(e -> ((UserException) e).getErrorCode())
        .isEqualTo(UserErrorCode.USER_NOT_FOUND);
  }

  @Test
  @DisplayName("메시지 목록 조회 성공")
  void findAllByChannelId_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    Channel channel = Channel.ofPublic("ch", "desc");
    Pageable pageable = Pageable.unpaged();
    
    Message message = new Message(new User("u", "e", "p"), channel, "content", Collections.emptyList());
    Slice<Message> messageSlice = new SliceImpl<>(List.of(message));
    MessageResponse messageResponse = new MessageResponse(UUID.randomUUID(), channelId, null, "content", Collections.emptyList(), Instant.now(), Instant.now());

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(messageRepository.findAllByChannelIdOrderByCreatedAtDesc(eq(channelId), any(Pageable.class)))
        .willReturn(messageSlice);
    given(messageMapper.toMessageResponse(any(Message.class))).willReturn(messageResponse);

    // when
    PageResponse<MessageResponse> response = messageService.findAllByChannelId(channelId, null, pageable);

    // then
    assertThat(response.content()).hasSize(1);
    assertThat(response.content().get(0).content()).isEqualTo("content");
  }

  @Test
  @DisplayName("메시지 목록 조회 실패 - 존재하지 않는 채널")
  void findAllByChannelId_Fail_ChannelNotFound() {
    // given
    UUID channelId = UUID.randomUUID();
    Pageable pageable = Pageable.unpaged();

    given(channelRepository.findById(channelId)).willReturn(Optional.empty()); // 채널 없음

    // when & then
    assertThatThrownBy(() -> messageService.findAllByChannelId(channelId, null, pageable))
        .isInstanceOf(ChannelException.class)
        .extracting(e -> ((ChannelException) e).getErrorCode())
        .isEqualTo(ChannelErrorCode.CHANNEL_NOT_FOUND);
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void update_Success() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");
    
    User author = new User("user", "email", "pw");
    Channel channel = Channel.ofPublic("ch", "desc");
    Message message = new Message(author, channel, "Old Content", Collections.emptyList());
    
    MessageResponse expectedResponse = new MessageResponse(
        messageId,
        UUID.randomUUID(),
        null,
        "Updated Content",
        Collections.emptyList(),
        Instant.now(),
        Instant.now()
    );

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toMessageResponse(any(Message.class))).willReturn(expectedResponse);

    // when
    MessageResponse response = messageService.update(messageId, request);

    // then
    assertThat(response.content()).isEqualTo("Updated Content");
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void update_Fail_MessageNotFound() {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty()); // 메시지 없음

    // when & then
    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageException.class)
        .extracting(e -> ((MessageException) e).getErrorCode())
        .isEqualTo(MessageErrorCode.MESSAGE_NOT_FOUND);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void delete_Success() {
    // given
    UUID messageId = UUID.randomUUID();

    // when
    messageService.deleteById(messageId);

    // then
    verify(messageRepository).deleteById(messageId);
  }
}
