package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    Channel createChannel(Channel channel);
    Channel updateChannel(Channel channel);
    boolean deleteChannel(UUID channelId);
    Channel getChannel(UUID channelId);
    List<Channel> getAllChannels();
}
