package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    /* Channel entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */
    Channel createChannel(Channel channel);
    Channel updateChannel(Channel channel);
    boolean deleteChannel(UUID channelId);
    Channel getChannel(UUID channelId);
    List<Channel> getAllChannels();
}
