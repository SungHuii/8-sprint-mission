package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.csrf.SpaCsrfTokenRequestHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler,
      LoginFailureHandler loginFailureHandler) throws Exception {
    return http
        // csrf 설정
        .csrf(csrf ->
            csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
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
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(
                    new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                .permitAll()
        )
        .exceptionHandling(exceptions ->
            exceptions
                // 인증되지 않은 사용자(로그인 안 한 상태)가 접근했을 때 로그인 페이지로 리다이렉트 하지 않고 401 에러를 반환
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        )
        // 최소한의 권한 설정 추가
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers("/", "/index.html").permitAll()
                // 토큰 발급, 회원가입, 그리고 로그인 API는 누구나 접근 가능해야 함
                .requestMatchers("/api/auth/csrf-token", "/api/users", "/api/auth/login")
                .permitAll()
                // 그 외의 요청은 인증 필요
                .anyRequest().authenticated()
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
}
