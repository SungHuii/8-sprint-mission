package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/* 슬라이스 테스트 */
@DataJpaTest
@ActiveProfiles("test")
/* @EnableJpaAuditing 대신 사용. 중복 제거, 유지보수 관리 시점을 생각해서 사용*/
@Import(JpaConfig.class) // Auditing 활성화
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Test
  @DisplayName("유저 저장 및 조회")
  void saveAndFind() {
    // given
    User user = new User("testuser", "test@test.com", "password");

    // when
    User savedUser = userRepository.save(user);

    // then
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getCreatedAt()).isNotNull(); // Auditing 확인
  }

  @Test
  @DisplayName("username으로 조회 성공")
  void findByUsername_Success() {
    // given
    User user = new User("findUser", "test@test.com", "pw");
    userRepository.save(user);

    // when
    Optional<User> foundUser = userRepository.findByUsername("findUser");

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test@test.com");
  }

  @Test
  @DisplayName("username으로 조회 실패 - 존재하지 않는 유저")
  void findByUsername_Fail() {
    // when
    Optional<User> foundUser = userRepository.findByUsername("unknownUser");

    // then
    assertThat(foundUser).isEmpty();
  }

  @Test
  @DisplayName("email로 조회 성공")
  void findByEmail_Success() {
    // given
    User user = new User("emailUser", "email@test.com", "pw");
    userRepository.save(user);

    // when
    Optional<User> foundUser = userRepository.findByEmail("email@test.com");

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("emailUser");
  }

  @Test
  @DisplayName("email로 조회 실패 - 존재하지 않는 이메일")
  void findByEmail_Fail() {
    // when
    Optional<User> foundUser = userRepository.findByEmail("unknown@test.com");

    // then
    assertThat(foundUser).isEmpty();
  }

  @Test
  @DisplayName("프로필과 함께 전체 조회 (findAllWithProfile)")
  void findAllWithProfile_Success() {
    // given
    // 1. 프로필 없는 유저
    User user1 = new User("user1", "user1@test.com", "pw");
    userRepository.save(user1);

    // 2. 프로필 있는 유저
    BinaryContent profile = new BinaryContent(UUID.randomUUID().toString(), "profile.jpg", 1024L);
    binaryContentRepository.save(profile);

    User user2 = new User("user2", "user2@test.com", "pw");
    user2.updateProfile(profile);
    userRepository.save(user2);

    // when
    List<User> users = userRepository.findAllWithProfile();

    // then
    assertThat(users).hasSize(2);
    // user2의 프로필이 로딩되었는지 확인
    User foundUser2 = users.stream().filter(u -> u.getUsername().equals("user2")).findFirst().get();
    assertThat(foundUser2.getProfile()).isNotNull();
    assertThat(foundUser2.getProfile().getOriginalName()).isEqualTo("profile.jpg");
  }

}
