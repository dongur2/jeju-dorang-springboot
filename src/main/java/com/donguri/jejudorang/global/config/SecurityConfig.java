package com.donguri.jejudorang.global.config;

import com.donguri.jejudorang.global.auth.oauth.OAuth2FailureHandler;
import com.donguri.jejudorang.global.auth.oauth.OAuth2SuccessHandler;
import com.donguri.jejudorang.global.auth.oauth.OAuth2UserService;
import com.donguri.jejudorang.global.auth.jwt.JwtAuthEntryPoint;
import com.donguri.jejudorang.global.auth.jwt.JwtAuthenticationFilter;
import com.donguri.jejudorang.global.auth.jwt.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final OAuth2UserService oAuth2UserService;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // 사용자 정보 조회, 인증 수행 구현체

        log.info("DAO authenticationProvier 생성");
        authProvider.setUserDetailsService(jwtUserDetailsService); // DB 사용자 정보 조회에 사용될 UserDetailsService 설정 - 사용자 제공한 인증 정보와 데이터베이스에 저장된 사용자 정보를 비교해 인증
        authProvider.setPasswordEncoder(passwordEncoder()); // 사용자 비밀번호 인코딩에 사용될 PasswordEncoder 설정

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception { // 사용자가 제공한 권한 기반 인증 -> Authentication 오브젝트 반환
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /*
    * securityFilterChain
    * HttpServletRequest(요청)에 대해 매칭될 수 있는 필터체인을 정의
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP 기본 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 로그인 비활성화

                // 로그아웃 -> 쿠키 삭제
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("access_token", "refresh_token")
                        .invalidateHttpSession(true))

                // 세션을 생성하거나 사용하지 않음
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // RequestMatcher를 사용해 HttpServletRequest(요청)에 대한 제한 설정
                .authorizeHttpRequests(
                        (authorizationManagerRequestMatcherRegistry
                                -> authorizationManagerRequestMatcherRegistry.requestMatchers(
                                        "/",
                                        "/admin",
                                        "/user/login", "/user/signup", "/user/signup/**", "/user/logout",
                                        "/login/**",
                                        "/email/**",
                                        "/trip/lists**", "/trip/places/*",
                                        "/community/boards/**",
                                        "/templates/**", "/error/**")
                                .permitAll()
                                .requestMatchers(
                                        "/user/quit",
                                        "/notifications/**",
                                        "/profile/**", "/mypage/**",
                                        "/community/post/**", "/tui-editor/**",
                                        "/community/comments/**",
                                        "/community/parties/{communityId}/state",
                                        "/bookmarks/**", "/bookmarks").authenticated()
                                .requestMatchers("/trip/api/data", "/admin/img").hasAuthority("ADMIN").anyRequest().authenticated()
                        )
                )

                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint)) // 예외 처리

                .authenticationProvider(authenticationProvider())// 사용자의 인증 정보를 제공하는 authenticationProvider 설정: 사용자 로그인 정보 기반 인증 수행

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth2 -> {
                    oauth2.userInfoEndpoint(endpointConfig -> endpointConfig.userService(oAuth2UserService));
                    oauth2.successHandler(oAuth2SuccessHandler);
                    oauth2.failureHandler(oAuth2FailureHandler);
                });

        return http.build();
    }

    /*
    * 필터 무시
    * */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers("/img/**", "/css/**", "/js/**", "/favicon.ico",
                        "/swagger-ui/**", "/api-docs", "/api-docs/json", "/v3/api-docs", "/error/**");
    }

}
