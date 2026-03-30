package com.sprint.mission.discodeit.config.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/*
 * CSR(SPA) 방식 - 프론트엔드가 서버로부터 HTML을 받지 않고 JSON 데이터만 API로 주고받음
 * Spring Security 6+ 환경에서 SPA 클라이언트와 안전하게 CSRF 토큰을 주고받기 위한 커스텀 핸들러
 */
public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

  // 원본 토큰 처리 핸들러 (HTTP 헤더로 들어온 토큰 검증 처리)
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  // XOR 마스킹된 토큰 처리 핸들러 (BREACH 공격 방어) 
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  // 서버가 클라이언트에 응답을 보낼 때 CSRF 토큰을 어떻게 핸들링할지 정의함
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      Supplier<CsrfToken> csrfToken) {

    // 응답 바디에 토큰이 렌더링될 경우를 대비해서, XOR 마스킹을 적용 -> BREACH 공격 방어
    this.xor.handle(request, response, csrfToken);

    // Spring Security의 CSRF 토큰은 기본적으로 지연로딩됨
    // 즉시 로드를 강제 호출해서 서버가 응답을 보내기 전에 'XSRF-TOKEN' 쿠키가 확실하게 생성되어 담기도록 함
    csrfToken.get();
  }

  // 클라이언트의 요청이 들어왔을 때, 해당 요청에서 CSRF 토큰을 찾아내는 방법 정의
  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {

    // 클라이언트 요청 헤더에서 토큰 값 가져오기
    String headerValue = request.getHeader(csrfToken.getHeaderName());

    /*
     * 판단 로직:
     * - 헤더에 토큰이 있다면 (SPA 방식): 프론트엔드가 쿠키에서 원본 토큰을 읽어 헤더에 담아 보낸 것
     * -> 마스킹 해제가 필요 없는 'plain' 핸들러를 사용해 토큰을 검증.
     * - 헤더에 토큰이 없다면 (전통적인 SSR Form 방식): HTML Form 파라미터(hidden input) 등으로 넘어온
     * 요청이므로, 마스킹된 토큰을 해석하기 위해 'xor' 핸들러를 사용.
     */
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request,
        csrfToken);
  }
}
