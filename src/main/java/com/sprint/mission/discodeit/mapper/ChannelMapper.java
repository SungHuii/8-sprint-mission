package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

  @Mapping(target = "participants", ignore = true)
  @Mapping(target = "lastMessageAt", ignore = true)
  ChannelResponse toChannelResponse(Channel channel);

}
