package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.enums.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.enums.CommonErrorCode;
import com.sprint.mission.discodeit.exception.enums.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @InjectMocks
  private BasicChannelService channelService;

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublic_Success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "General Chat");
    Channel channel = Channel.ofPublic(request.name(), request.description());
    ChannelResponse expectedResponse = new ChannelResponse(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        "General",
        "General Chat",
        null,
        Collections.emptyList()
    );

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(channelMapper.toChannelResponse(any(Channel.class), any(), any())).willReturn(
        expectedResponse);

    // when
    ChannelResponse response = channelService.createPublic(request);

    // then
    assertThat(response.name()).isEqualTo(request.name());
    assertThat(response.type()).isEqualTo(ChannelType.PUBLIC);
    verify(channelRepository).save(any(Channel.class));
  }

  @Test
  @DisplayName("비공개 채널 생성 성공")
  void createPrivate_Success() {
    // given
    UUID userId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
    Channel channel = Channel.ofPrivate();
    User user = new User("user", "email", "pw");

    ChannelResponse expectedResponse = new ChannelResponse(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        null,
        null,
        null,
        Collections.emptyList()
    );

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelMapper.toChannelResponse(any(Channel.class), any(), any())).willReturn(
        expectedResponse);

    // when
    ChannelResponse response = channelService.createPrivate(request);

    // then
    assertThat(response.type()).isEqualTo(ChannelType.PRIVATE);
    verify(readStatusRepository).save(any());
  }

  @Test
  @DisplayName("비공개 채널 생성 실패 - 존재하지 않는 참여자")
  void createPrivate_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));
    Channel channel = Channel.ofPrivate();

    given(channelRepository.save(any(Channel.class))).willReturn(channel);
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> channelService.createPrivate(request))
        .isInstanceOf(UserException.class)
        .extracting(e -> ((UserException) e).getErrorCode())
        .isEqualTo(UserErrorCode.USER_NOT_FOUND);
  }

  @Test
  @DisplayName("유저별 채널 목록 조회 성공")
  void findAllByUserId_Success() {
    // given
    UUID userId = UUID.randomUUID();

    // 1. 공개 채널
    Channel publicChannel = Channel.ofPublic("Public", "Desc");

    // 2. 비공개 채널 (참여 중)
    Channel privateChannel = Channel.ofPrivate();
    User user = new User("user", "email", "pw");
    ReadStatus readStatus = new ReadStatus(user, privateChannel, java.time.Instant.now());

    ChannelResponse publicResponse = new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC,
        "Public", "Desc", null, Collections.emptyList());
    ChannelResponse privateResponse = new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE,
        null, null, null, Collections.emptyList());

    given(channelRepository.findAll()).willReturn(List.of(publicChannel));
    given(readStatusRepository.findAllByUserId(userId)).willReturn(List.of(readStatus));
    given(channelRepository.findAllById(anyList())).willReturn(List.of(privateChannel));

    // Mapper 2번 호출 (공개 1번, 비공개 1번)
    given(channelMapper.toChannelResponse(any(Channel.class), any(), any()))
        .willReturn(publicResponse) // 첫 번째 호출
        .willReturn(privateResponse); // 두 번째 호출

    // when
    List<ChannelResponse> responses = channelService.findAllByUserId(userId);

    // then
    assertThat(responses).hasSize(2);
  }

  @Test
  @DisplayName("유저별 채널 목록 조회 실패 - userId null")
  void findAllByUserId_Fail_InvalidInput() {
    // when & then
    assertThatThrownBy(() -> channelService.findAllByUserId(null))
        .isInstanceOf(DiscodeitException.class)
        .extracting(e -> ((DiscodeitException) e).getErrorCode())
        .isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE);
  }

  @Test
  @DisplayName("채널 수정 성공")
  void update_Success() {
    // given
    UUID channelId = UUID.randomUUID();
    ChannelUpdateRequest request = new ChannelUpdateRequest("New Name", "New Desc");
    Channel channel = Channel.ofPublic("Old Name", "Old Desc");
    ChannelResponse expectedResponse = new ChannelResponse(
        channelId,
        ChannelType.PUBLIC,
        "New Name",
        "New Desc",
        null,
        Collections.emptyList()
    );

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(channelMapper.toChannelResponse(any(Channel.class), any(), any())).willReturn(
        expectedResponse);

    // when
    ChannelResponse response = channelService.update(channelId, request);

    // then
    assertThat(response.name()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("채널 수정 실패 - 비공개 채널은 수정 불가")
  void update_Fail_PrivateChannel() {
    // given
    UUID channelId = UUID.randomUUID();
    ChannelUpdateRequest request = new ChannelUpdateRequest("New Name", null);
    Channel privateChannel = Channel.ofPrivate();

    given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

    // when & then
    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelException.class)
        .extracting(e -> ((ChannelException) e).getErrorCode())
        .isEqualTo(ChannelErrorCode.PRIVATE_CHANNEL_UPDATE_NOT_ALLOWED);
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void delete_Success() {
    // given
    UUID channelId = UUID.randomUUID();

    // when
    channelService.deleteById(channelId);

    // then
    verify(messageRepository).deleteAllByChannelId(channelId);
    verify(readStatusRepository).deleteAllByChannelId(channelId);
    verify(channelRepository).deleteById(channelId);
  }
}
