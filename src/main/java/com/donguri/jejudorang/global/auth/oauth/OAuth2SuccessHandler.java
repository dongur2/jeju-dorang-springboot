package com.donguri.jejudorang.global.auth.oauth;

import com.donguri.jejudorang.global.auth.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final int cookieTime;
    @Autowired private final JwtProvider jwtProvider;

    public OAuth2SuccessHandler(JwtProvider jwtProvider, @Value("${jwt.cookie-expire}") int cookieTime) {
        this.jwtProvider = jwtProvider;
        this.cookieTime = cookieTime;
    }

    /*
    * OAuth 로그인 성공시 수행: 토큰 발급 & 리다이렉트
    *
    * */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("** OAuth2 Login SUCCESS **");

        try {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();

            // JWT 토큰 발급
            String accessToken = jwtProvider.generateOAuth2AccessToken(oauth2User);
            String refreshToken = jwtProvider.generateOAuth2RefreshToken(oauth2User);

            // 토큰 쿠키에 설정
            setCookiesForToken(response, accessToken, refreshToken);

            // 로그인 성공 후 리다이렉트 -> 홈
            response.sendRedirect("/");

        } catch (Exception e) {
            log.error("onAuthenticationSuccess error: {}", e.getMessage());
            throw e;
        }
    }

    private void setCookiesForToken(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie cookieForAT = new Cookie("access_token", accessToken);
        Cookie cookieForRT = new Cookie("refresh_token", refreshToken);

        cookieForAT.setPath("/");
        cookieForRT.setPath("/");

        cookieForAT.setMaxAge(cookieTime);
        cookieForRT.setMaxAge(cookieTime);

        cookieForAT.setHttpOnly(true);
        cookieForRT.setHttpOnly(true);

        response.addCookie(cookieForAT);
        response.addCookie(cookieForRT);

        log.info("쿠키 업데이트 완료");
    }
}
