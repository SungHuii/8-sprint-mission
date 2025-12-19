package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    // create
    UserStatusResponse create(UserStatusCreateRequest request);

    // find
    UserStatusResponse findById(UUID userStatusId);
    List<UserStatusResponse> findAll();

    // update
    UserStatusResponse update(UserStatusUpdateRequest request);
    UserStatusResponse updateByUserId(UserStatusUpdateByUserIdRequest request);

    // delete
    void deleteById(UUID userStatusId);
}
