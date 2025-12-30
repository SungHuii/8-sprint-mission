package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
  /* User entity CRUD service
   * */

  // create
  UserResponse create(UserCreateRequest request);

  // read
  UserResponse findById(UUID userId);

  List<UserResponse> findAll();

  List<UserDto> findAllUserDtos();

  // update
  UserResponse update(UserUpdateRequest request);

  // delete
  void deleteById(UUID userId);
}

