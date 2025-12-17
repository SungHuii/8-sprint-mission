package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    /* JCF(Java Collections Framework) 기반으로 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화
     * data 필드를 활용해서 CRUD 메소드 구현
     */

    private final Map<UUID, Channel> data;
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

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
    public Channel saveChannel(String name, String description) {
        return new Channel(name, description);
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
        if (channelRemoved == null ) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return false;
        }
        System.out.println("채널이 성공적으로 삭제되었습니다.");
        return true;
    }

    @Override
    public Channel findById(UUID channelId) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return null;
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }
}
