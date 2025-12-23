package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

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

    // update
    UserResponse update(UserUpdateRequest request);

    // delete
    void deleteById(UUID userId);

    /*
    Spring 도입 이전 코드
    @Deprecated
    User save(User user);
    User saveUser(String name, String nickname, String phoneNumber, String password, String email);
    User updateUser(User user);
    void deleteById(UUID userid);
    User findById(UUID userId);
    List<User> findAll();
    */
}

