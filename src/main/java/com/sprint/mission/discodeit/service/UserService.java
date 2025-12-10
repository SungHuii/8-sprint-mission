package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    /* User entity CRUD service
    * 생성 / 읽기 / 모두 읽기 / 수정 / 삭제 기능
    * */
    User createUser(User user);
    User updateUser(User user);
    boolean deleteUser(UUID userid);
    User getUser(UUID userId);
    List<User> getAllUsers();

}
