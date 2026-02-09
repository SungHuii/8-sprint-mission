package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

  @Mapping(target = "author", source = "author")
  @Mapping(target = "channelId", source = "channel.id")
  @Mapping(target = "updatedAt", source = "updatedAt")
  MessageResponse toMessageResponse(Message message);
}
