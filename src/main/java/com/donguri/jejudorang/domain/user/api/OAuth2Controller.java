package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.response.KakaoTokenResponse;
import com.donguri.jejudorang.domain.user.dto.response.KakaoUserResponse;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@Controller
public class OAuth2Controller {

    private final int cookieTime;
    @Autowired private final UserService userService;
    @Autowired private final OAuth2UserService oAuth2UserService;

    public OAuth2Controller(UserService userService, @Value("${jwt.cookie-expire}") int cookieTime, OAuth2UserService oAuth2UserService) {
        this.userService = userService;
        this.cookieTime = cookieTime;
        this.oAuth2UserService = oAuth2UserService;
    }

//    @GetMapping("/login/oauth2/code/kakao")
//    public void loginKakao(@RequestParam("code") String code, HttpServletResponse response) {
//
//        // 인가 코드
//        log.info("Kakao Authentication Code : {}", code);
//
//        KakaoTokenResponse accessToken = userService.requestAccessToken(code);
//        log.info("access token: {}", accessToken);
//
////        userService.getKakaoUserInfo(accessToken);
//
//        // 카카오 토큰 발급
////        OAuth2AccessToken kakaoToken = userService.getKakaoToken(code);
////        log.info("KakaoTokenResponse : {}", kakaoToken);
////
////        // 카카오 유저 정보 접근
////        OAuth2UserInfo userInfo = userService.getUserInfo(kakaoToken.getTokenValue());
////        log.info("KakaoUserResponse: {}", userInfo);
//
//        // JWT
////        oAuth2UserService.loadUser();
//
//
////        return "code: "+username;
////        log.info("username from kakao: {}", username);
////        return "redirect:/";
//    }

    private void setCookieForToken(Map<String, String> tokens, HttpServletResponse response) {
        tokens.forEach((index, token) -> {
            Cookie newCookieToAdd = new Cookie(index, token);
            newCookieToAdd.setHttpOnly(true);
            newCookieToAdd.setMaxAge(cookieTime);
            newCookieToAdd.setPath("/");
            response.addCookie(newCookieToAdd);
        });
    }
}
