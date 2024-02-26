package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.repository.UserRepository;
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
import java.util.Map;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private final JwtProvider jwtProvider;
    @Autowired private final UserRepository userRepository;
    private final int cookieTime;

    public OAuth2SuccessHandler(JwtProvider jwtProvider, UserRepository userRepository, @Value("${jwt.cookie-expire}") int cookieTime) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.cookieTime = cookieTime;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("** OAuth2 Login SUCCESS **");

        try {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();

            log.info("oauth2User: {}", oauth2User);

            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
            log.info("email: {}, authorities: {}, id: {}", kakaoAccount.get("email").toString(), oauth2User.getAuthorities(), oauth2User.getName());

            String accessToken = jwtProvider.generateOAuth2AccessToken(oauth2User);
            String refreshToken = jwtProvider.generateOAuth2RefreshToken(oauth2User);

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

        } catch (Exception e) {
            log.error("onAuthenticationSuccess error: {}", e.getMessage());
        }


    }
}
