package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserSummaryResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

  // create
  UserResponse create(UserCreateRequest request);

  // find
  List<UserResponse> findAll();

  List<UserSummaryResponse> findAllUserSummaries();

  // update
  UserResponse update(UUID userId, UserUpdateRequest request);

  // delete
  void deleteById(UUID userId);
}
