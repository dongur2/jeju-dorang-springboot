package com.donguri.jejudorang.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Config 파일로 설정
@EnableWebSecurity // WebSecurity 활성화
public class SecurityConfiguration {

    @Bean // 임시 전체 경로 접근 허가
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] staticResources  =  {
                "/**",
        };

        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(staticResources).permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
}
