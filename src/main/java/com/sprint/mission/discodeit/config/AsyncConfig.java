package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig implements AsyncConfigurer {

  @Bean(name = "eventTaskExecutor")
  public ThreadPoolTaskExecutor eventTaskExecutor() {

    return buildExecutor(5, 10, 25, 60, "Event-Async");
  }

  private ThreadPoolTaskExecutor buildExecutor(int core, int max, int queue, int keepAlive,
      String prefix) {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    // 스레드 풀 세부 스펙 설정
    executor.setCorePoolSize(core);
    executor.setMaxPoolSize(max);
    executor.setQueueCapacity(queue);
    executor.setKeepAliveSeconds(keepAlive);
    executor.setThreadNamePrefix(prefix);

    executor.setTaskDecorator(new ContextCopyingDecorator());

    executor.initialize();
    return executor;
  }

  private static class ContextCopyingDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

      // 기존 스레드 정보 복사
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      Map<String, String> mdcContext = MDC.getCopyOfContextMap();

      return () -> {
        try {
          // 새로운 스레드에 복사한 정보 붙여넣기
          SecurityContext newContext = SecurityContextHolder.createEmptyContext();
          newContext.setAuthentication(authentication);
          SecurityContextHolder.setContext(newContext);

          if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
          }

          // 비동기 로직 실행
          runnable.run();
        } finally {
          // 클리어
          SecurityContextHolder.clearContext();
          MDC.clear();
        }
      };
    }
  }
}
