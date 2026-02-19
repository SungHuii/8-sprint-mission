package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/* 요청이 들어올 때 MDC에 정보를 넣고, 나갈 때 지우는 역할 */
@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";
  private static final String MDC_REQUEST_ID = "request_id";
  private static final String MDC_METHOD = "method";
  private static final String MDC_URI = "uri";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {

    String requestId = UUID.randomUUID().toString().substring(0, 8); // 8자리만 사용

    // MDC에 값 저장
    MDC.put(MDC_REQUEST_ID, requestId);
    MDC.put(MDC_METHOD, request.getMethod());
    MDC.put(MDC_URI, request.getRequestURI());

    // 응답 헤더에 요청 ID 추가 (클라이언트 추적 가능하게)
    response.setHeader(REQUEST_ID_HEADER, requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {

    // 요청 처리가 끝나면 MDC 정리 (ThreadLocal이므로 필수로 해줘야 함)
    MDC.clear();
  }
}
