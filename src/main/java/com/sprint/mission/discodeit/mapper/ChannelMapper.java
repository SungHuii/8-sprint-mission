package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.entity.Channel;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChannelMapper {

  @Mapping(target = "participants", source = "participants")
  @Mapping(target = "lastMessageAt", source = "lastMessageAt")
  ChannelResponse toChannelResponse(Channel channel, List<UserSummaryResponse> participants, Instant lastMessageAt);

}
