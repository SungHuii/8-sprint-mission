package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    /* User entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */

    @Deprecated
    User save(User user);
    User saveUser(String name, String nickname, String phoneNumber, String password, String email);
    User updateUser(User user);
    void deleteById(UUID userid);
    User findById(UUID userId);
    List<User> findAll();

}
