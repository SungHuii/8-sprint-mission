package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("채널 저장 및 조회 (Auditing 확인)")
  void saveAndFind() {
    // given
    Channel channel = Channel.ofPublic("General", "General Chat");

    // when
    Channel savedChannel = channelRepository.save(channel);

    // then
    assertThat(savedChannel.getId()).isNotNull();
    assertThat(savedChannel.getCreatedAt()).isNotNull();
    assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(savedChannel.getName()).isEqualTo("General");
  }

  @Test
  @DisplayName("전체 채널 조회")
  void findAll_Success() {
    // given
    Channel ch1 = Channel.ofPublic("Ch1", "Desc1");
    Channel ch2 = Channel.ofPrivate();
    channelRepository.saveAll(List.of(ch1, ch2));

    // when
    List<Channel> channels = channelRepository.findAll();

    // then
    assertThat(channels).hasSize(2);
  }

  @Test
  @DisplayName("ID로 조회 실패 - 존재하지 않는 채널")
  void findById_Fail() {
    // when
    Optional<Channel> found = channelRepository.findById(UUID.randomUUID());

    // then
    assertThat(found).isEmpty();
  }
}
