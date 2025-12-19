package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    /* Channel entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */

    // create
    ChannelResponse createPublic(PublicChannelCreateRequest request);
    ChannelResponse createPrivate(PrivateChannelCreateRequest request);

    // find
    ChannelResponse findById(UUID channelId);

    // findAll -> findAllByUserId 로 변경
    List<ChannelResponse> findAllByUserId(UUID userId);

    // update
    ChannelResponse update(ChannelUpdateRequest request);

    // delete
    void deleteById(UUID channelId);

    /*
    Spring 도입에 따라 주석 처리
    @Deprecated
    Channel save(Channel channel);
    Channel saveChannel(String name, String description);
    Channel updateChannel(Channel channel);
    boolean deleteChannel(UUID channelId);
    Channel findById(UUID channelId);
    List<Channel> findAll();*/
}
