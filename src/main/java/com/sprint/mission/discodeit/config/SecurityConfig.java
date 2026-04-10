package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.csrf.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String REMEMBER_ME_KEY = "${security.remember-me.key}";

  @Value(REMEMBER_ME_KEY)
  private String rememberMeKey;

  @Bean
  @Profile("dev")
  public CommandLineRunner debugFilterChain(SecurityFilterChain filterChain) {
    return args -> {
      int filterSize = filterChain.getFilters().size();

      List<String> filterNames = IntStream.range(0, filterSize)
          .mapToObj(idx -> String.format("\t[%s/%s] %s", idx + 1, filterSize,
              filterChain.getFilters().get(idx).getClass()))
          .toList();

      log.debug("현재 적용된 필터 체인 목록:");
      filterNames.forEach(log::debug);
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      SessionRegistry sessionRegistry,
      DiscodeitUserDetailsService discodeitUserDetailsService,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
    return http
        // csrf 설정
        .csrf(csrf ->
            csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )
        // 권한 설정
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers("/", "/index.html").permitAll()
                // 토큰 발급, 회원가입, 그리고 로그인 API는 누구나 접근 가능해야 함
                .requestMatchers("/api/auth/csrf-token", "/api/users", "/api/auth/login",
                    "/api/auth/logout")
                .permitAll()
                // Swagger, API 문서 경로, Actuator
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                // 그 외의 요청은 인증 필요
                .anyRequest().authenticated()
        )
        // 세션 관리 설정
        .sessionManagement(management ->
            management
                .sessionConcurrency(concurrency ->
                    concurrency
                        .maximumSessions(1) // 동시 세션 1개만 허용
                        .maxSessionsPreventsLogin(
                            false) // 새로 로그인 시 -> true : 새 로그인 차단, false : 기존 세션 만료
                        .sessionRegistry(sessionRegistry)
                )
        )
        // Remember-me 설정
        .rememberMe(rememberMe ->
            rememberMe
                // 고정 키 설정
                .key(rememberMeKey)
                // 7일 유지 설정
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .userDetailsService(discodeitUserDetailsService)
        )
        // formLogin 설정
        .formLogin(form ->
            form.loginProcessingUrl("/api/auth/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
        )
        // 로그아웃 설정
        .logout(logout ->
            logout
                .logoutUrl("/api/auth/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                .permitAll()
        )
        .exceptionHandling(exceptions ->
            exceptions
                // 인증되지 않은 사용자(로그인 안 한 상태)가 접근했을 때 로그인 페이지로 리다이렉트 하지 않고 401 에러를 반환
                .authenticationEntryPoint((request, response, authException) -> {
                  resolver.resolveException(request, response, null, authException);
                })
                // 403 에러(권한 없음) 처리
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                  resolver.resolveException(request, response, null, accessDeniedException);
                })
        )
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> web
        .ignoring()
        // 브라우저 기본 요청 및 에러 페이지
        .requestMatchers("/favicon.ico", "/error")
        // 정적 리소스
        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/assets/**");
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();

    // ADMIN > CHANNEL_MANAGER > USER
    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER > ROLE_USER");
    return roleHierarchy;
  }

  @Bean
  static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  // 세션이 만료될 때 SessionRegistry에도 자동 반영
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}
