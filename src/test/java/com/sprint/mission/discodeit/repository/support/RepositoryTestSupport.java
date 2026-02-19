package com.sprint.mission.discodeit.repository.support;

import com.sprint.mission.discodeit.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
public abstract class RepositoryTestSupport {
  /*
   * 이 Support 추상 클래스는 테스트 코드의 응집도, 결합도를 생각했을 때
   * 불필요한 의존성을 막고, 필요한 것만 해당 테스트 클래스에 직접 주입하도록 하는 것이 맞다고 생각되어
   * 공통 설정 어노테이션만 공유하도록 했습니다.
   * */
}
