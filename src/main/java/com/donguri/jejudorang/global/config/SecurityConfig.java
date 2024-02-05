package com.donguri.jejudorang.global.config;

import com.donguri.jejudorang.global.config.jwt.JwtAuthEntryPoint;
import com.donguri.jejudorang.global.config.jwt.JwtAuthenticationFilter;
import com.donguri.jejudorang.global.config.jwt.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired private final JwtUserDetailsService jwtUserDetailsService;

    @Autowired private final JwtAuthEntryPoint jwtAuthEntryPoint;

    public SecurityConfig(JwtUserDetailsService jwtUserDetailsService, JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

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
        log.info("** init of http security START ** ");

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
                                        "/user/login", "/user/signup", "/user/signup/**", "/user/logout", "/user/email/**",
                                        "/trip/lists/*", "/trip/places/*",
                                        "/community/boards/**",
                                        "/templates/**", "/error/**")
                                .permitAll()
                                .requestMatchers(
                                        "/user/settings/profile/**",
                                        "/community/post/**", "/tui-editor/**",
                                        "/community/parties/{communityId}/state",
                                        "/bookmarks/communities/{communityId}",
                                        "/bookmarks/trips/{tripId}",
                                        "/trip/api/data"
                                ).authenticated()
                        )
                )

                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint)) // 예외 처리

                .authenticationProvider(authenticationProvider())// 사용자의 인증 정보를 제공하는 authenticationProvider 설정: 사용자 로그인 정보 기반 인증 수행

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
    * 필터 무시
    * */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers("/img/**", "/css/**", "/js/**", "/favicon.ico");
    }

}
