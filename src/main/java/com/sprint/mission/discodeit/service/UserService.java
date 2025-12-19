package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    /* User entity CRUD service
    * ?앹꽦 / ?쎄린 / 紐⑤몢 ?쎄린 / ?섏젙 / ??젣 湲곕뒫
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
    Spring ?꾩엯???곕씪 二쇱꽍 泥섎━
    @Deprecated
    User save(User user);
    User saveUser(String name, String nickname, String phoneNumber, String password, String email);
    User updateUser(User user);
    void deleteById(UUID userid);
    User findById(UUID userId);
    List<User> findAll();
    */
}

