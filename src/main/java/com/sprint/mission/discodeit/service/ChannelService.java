package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    /* Channel entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */
    @Deprecated
    Channel save(Channel channel);
    Channel saveChannel(String name, String description);
    Channel updateChannel(Channel channel);
    boolean deleteChannel(UUID channelId);
    Channel findById(UUID channelId);
    List<Channel> findAll();
}
