package com.donguri.jejudorang.global.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/*
* Spring Security에서 사용자가 인증되지 않았을 때 호출
* - 인증되지 않은 사용자가 보호된 리소스에 접근을 시도할 때 호출
* */
@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Autowired JwtProvider jwtProvider;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        /*
        * 로그인하지 않았거나, 유효한 쿠키가 아닐 경우 로그인 화면으로 리다이렉트 (401: 인증되지 않음)
        * */
        if(request.getCookies() == null || Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getName().equals("access_token"))
        || !jwtProvider.validateJwtToken(Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("access_token")).toList().get(0).getValue())) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/user/login");

        /*
        * 로그인했지만 권한이 없을 경우 403 에러 페이지로 리다이렉트 (403: 권한 없음)
        * */
        } else {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
