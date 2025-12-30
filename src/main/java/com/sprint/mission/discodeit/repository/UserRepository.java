package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  User save(User user);

  User updateUser(User user);

  List<User> findAll();

  Optional<User> findById(UUID userId);

  Optional<User> findByEmail(String email);

  Optional<User> findByNickname(String nickname);

  void deleteById(UUID userId);

}
