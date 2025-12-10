package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();


    @Override
    public Channel createChannel(Channel channel) {
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
        if (existingChannel != null) {
            existingChannel.updateChName(channel.getChName());
            existingChannel.updateChDescription(channel.getChDescription());
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        return existingChannel;
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channelRemoved = data.remove(channelId);
        if (channelRemoved != null ) {
            System.out.println("채널이 성공적으로 삭제되었습니다.");
            return true;
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return false;
        }
    }

    @Override
    public Channel getChannel(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        List<Channel> allChannels = new ArrayList<>(data.values());
        return allChannels;
    }
}
