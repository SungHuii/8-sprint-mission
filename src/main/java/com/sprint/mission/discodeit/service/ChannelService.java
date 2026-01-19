package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
  /* Channel entity CRUD service
   * */

  // create
  ChannelResponse createPublic(PublicChannelCreateRequest request);

  ChannelResponse createPrivate(PrivateChannelCreateRequest request);

  // find
  ChannelResponse findById(UUID channelId);

  // findAll -> findAllByUserId
  List<ChannelResponse> findAllByUserId(UUID userId);

  List<ChannelResponse> findAll();

  // update
  ChannelResponse update(UUID channelId, ChannelUpdateRequest request);

  // delete
  void deleteById(UUID channelId);
}
