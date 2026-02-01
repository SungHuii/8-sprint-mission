package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

  // readStatus.user (User 객체) -> response.userId (UUID) 이기 때문에, 이름도 다르고 타입이 달라서 자동 매핑이 안될 수 있다.
  // 따라서 @Mapping으로 경로를 지정
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "channel.id", target = "channelId")
  ReadStatusResponse toReadStatusResponse(ReadStatus readStatus);
}
