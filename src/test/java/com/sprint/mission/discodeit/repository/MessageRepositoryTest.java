package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;

  private User user;
  private Channel channel;

  @BeforeEach
  void setUp() {
    // 테스트용 유저와 채널 생성
    user = userRepository.save(new User("user", "test@test.com", "pw"));
    channel = channelRepository.save(Channel.ofPublic("ch", "desc"));
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 (최신순 정렬, 페이징)")
  void findAllByChannelIdOrderByCreatedAtDesc() throws InterruptedException {
    // given
    // 메시지 생성
    Message msg1 = messageRepository.save(
        new Message(user, channel, "msg1", Collections.emptyList()));
    Thread.sleep(10); // 시간 차이 두기
    Message msg2 = messageRepository.save(
        new Message(user, channel, "msg2", Collections.emptyList()));
    Thread.sleep(10); // 시간 차이 두기
    Message msg3 = messageRepository.save(
        new Message(user, channel, "msg3", Collections.emptyList()));

    // 다른 채널 메시지
    Channel otherChannel = channelRepository.save(Channel.ofPublic("otherCh", "desc"));
    messageRepository.save(
        new Message(user, otherChannel, "otherChannelMsg", Collections.emptyList()));

    // when
    // 페이지 크기 2로 조회
    Slice<Message> result = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(
        channel.getId(), PageRequest.of(0, 2));

    // then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getContent()).isEqualTo("msg3"); // 최신순이므로 msg3이 먼저
    assertThat(result.getContent().get(1).getContent()).isEqualTo("msg2");
    assertThat(result.hasNext()).isTrue(); // 다음 페이지(msg1)가 있으므로 true
  }

  @Test
  @DisplayName("채널의 마지막 메시지 조회")
  void findTopByChannelIdOrderByCreatedAtDesc() throws InterruptedException {
    // given
    messageRepository.save(new Message(user, channel, "old", Collections.emptyList()));
    Thread.sleep(10); // 시간 차이 두기
    messageRepository.save(new Message(user, channel, "new", Collections.emptyList()));

    // when
    Optional<Message> lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(
        channel.getId());

    // then
    assertThat(lastMessage).isPresent();
    assertThat(lastMessage.get().getContent()).isEqualTo("new");
  }

  @Test
  @DisplayName("다른 채널의 메시지는 조회하면 안됨")
  void findAllByChannelId_Fail_WrongChannel() {
    // given
    Channel otherChannel = channelRepository.save(Channel.ofPublic("otherCh", "desc"));
    messageRepository.save(new Message(user, otherChannel, "otherMsg", Collections.emptyList()));

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdOrderByCreatedAtDesc(
        channel.getId(), PageRequest.of(0, 10));

    // then
    assertThat(result.getContent()).isEmpty();
  }


  @Test
  @DisplayName("메시지가 없는 채널 조회 시 빈 결과를 반환해야 함")
  void findTopByChannelId_Fail_NoMessage() {
    // given
    // 메시지 저장 안 함

    // when
    Optional<Message> lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(
        channel.getId());

    // then
    assertThat(lastMessage).isEmpty();
  }
}
