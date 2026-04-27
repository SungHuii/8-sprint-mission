package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {

    // 메모리 기반 SimpleBroker 사용
    // 클라이언트에서 메시지 구독 시 prefix
    config.enableSimpleBroker("/sub");
    // 클라이언트에서 메시지 발행 시 prefix
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {

    // STOMP 엔드포인트 /ws 설정, SockJS 연결 지원
    registry.addEndpoint("/ws")
        .withSockJS();
  }
}
