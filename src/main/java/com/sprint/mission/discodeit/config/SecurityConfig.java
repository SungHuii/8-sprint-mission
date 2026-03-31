package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.csrf.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler,
      SessionRegistry sessionRegistry) throws Exception {
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
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                // 403 에러(권한 없음) 처리
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                  response.setStatus(HttpStatus.FORBIDDEN.value());
                })
        )
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
