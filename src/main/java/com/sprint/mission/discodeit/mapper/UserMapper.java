package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binary.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "online", ignore = true) // 서비스에서 별도 설정 필요
    UserResponse toUserResponse(User user);

    @Mapping(target = "online", ignore = true) // 서비스에서 별도 설정 필요
    UserSummaryResponse toUserSummaryResponse(User user);

    BinaryContentResponse toBinaryContentResponse(BinaryContent binaryContent);
}
