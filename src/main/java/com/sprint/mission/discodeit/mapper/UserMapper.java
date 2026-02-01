package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.auth.AuthResponse;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

    @Mapping(target = "online", source = "isOnline")
    UserResponse toUserResponse(User user, boolean isOnline);

    @Mapping(target = "online", source = "isOnline")
    UserSummaryResponse toUserSummaryResponse(User user, boolean isOnline);

    @Mapping(target = "online", constant = "false")
    UserSummaryResponse toUserSummaryResponse(User user);

    @Mapping(target = "online", source = "isOnline")
    AuthResponse toAuthResponse(User user, boolean isOnline);
}
