package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  // 기본 findAll()은 오버라이드하지 않고 그대로 둠 (Lazy 로딩)

  // 프로필까지 한 번에 가져오는 전용 메서드
  @EntityGraph(attributePaths = {"profile"})
  @Query("SELECT u FROM User u")
  List<User> findAllWithProfile();
}
