package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.config.RepoProps;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
    prefix = RepoProps.PREFIX,
    name = RepoProps.TYPE_NAME,
    havingValue = RepoProps.TYPE_JCF,
    matchIfMissing = true
)
public class JCFChannelRepository implements ChannelRepository {

  private final Map<UUID, Channel> data = new ConcurrentHashMap<>();


  @Override
  public Channel save(Channel channel) {
    if (data.containsKey(channel.getId())) {
      System.out.println("이미 존재하는 채널입니다.");
      return null;
    }
    data.put(channel.getId(), channel);
    return channel;
  }

  @Override
  public Channel updateChannel(Channel channel) {
    Channel existingChannel = data.get(channel.getId());
    if (existingChannel == null) {
      System.out.println("해당 채널을 찾을 수 없습니다.");
      return null;
    }
    existingChannel.updateChName(channel.getChName());
    existingChannel.updateChDescription(channel.getChDescription());
    System.out.println("채널이 성공적으로 업데이트되었습니다.");

    return existingChannel;
  }

  @Override
  public boolean deleteChannel(UUID channelId) {
    Channel channelRemoved = data.remove(channelId);
    if (channelRemoved == null) {
      System.out.println("해당 채널을 찾을 수 없습니다.");
      return false;
    }
    System.out.println("채널이 성공적으로 삭제되었습니다.");

    return true;
  }

  @Override
  public Optional<Channel> findById(UUID channelId) {
    return Optional.ofNullable(data.get(channelId));
  }

  @Override
  public List<Channel> findAll() {
    return new ArrayList<>(data.values());
  }
}
