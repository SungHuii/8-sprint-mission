package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    /* Channel entity CRUD service
    * ?앹꽦 / ?쎄린 / 紐⑤몢 ?쎄린 / ?섏젙 / ??젣 湲곕뒫
    * */

    // create
    ChannelResponse createPublic(PublicChannelCreateRequest request);
    ChannelResponse createPrivate(PrivateChannelCreateRequest request);

    // find
    ChannelResponse findById(UUID channelId);

    // findAll -> findAllByUserId 濡?蹂寃?
    List<ChannelResponse> findAllByUserId(UUID userId);

    // update
    ChannelResponse update(ChannelUpdateRequest request);

    // delete
    void deleteById(UUID channelId);

    /*
    Spring ?꾩엯???곕씪 二쇱꽍 泥섎━
    @Deprecated
    Channel save(Channel channel);
    Channel saveChannel(String name, String description);
    Channel updateChannel(Channel channel);
    boolean deleteChannel(UUID channelId);
    Channel findById(UUID channelId);
    List<Channel> findAll();*/
}

