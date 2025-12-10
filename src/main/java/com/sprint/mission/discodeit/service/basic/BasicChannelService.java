package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }


    @Override
    public Channel createChannel(Channel channel) {
        if (channel.getChName() == null || channel.getChName().isEmpty()) {
            System.out.println("이름이 비어있습니다.");
            return null;
        }
        return  channelRepository.createChannel(channel);
    }

    @Override
    public Channel updateChannel(Channel channel) {
        Channel checkExisted = channelRepository.getChannel(channel.getId());

        if (checkExisted == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
        checkExisted.updateChName(channel.getChName());
        checkExisted.updateChDescription(channel.getChDescription());

        return channelRepository.updateChannel(channel);
    }

    @Override
    public boolean deleteChannel(UUID channelId) {
        return channelRepository.deleteChannel(channelId);
    }

    @Override
    public Channel getChannel(UUID channelId) {
        return channelRepository.getChannel(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.getAllChannels();
    }
}
