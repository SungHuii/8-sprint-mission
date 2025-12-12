package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    User save(User user);
    User updateUser(User user);
    boolean deleteUser(UUID userId);
    User findById(UUID userId);
    List<User> findAll();
}
