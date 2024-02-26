package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.entity.LoginType;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2UserService CUSTOM - UserRequest: {}", userRequest);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("attributes :: " + attributes);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(), "id");

//
//        ClientRegistration clientRegistration = userRequest.getClientRegistration();
//        log.info("clientRegistration: {}", clientRegistration);
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        oAuth2User.getAuthorities().forEach(auth -> log.info("Authority: {}", auth));
//
//        OAuth2UserInfo oAuth2UserInfo = null;
//        if (clientRegistration.getRegistrationId().equals("kakao")) {
//            log.info("***** Kakao Login *****");
//            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
//            log.info("oauth2userInfo : {}", oAuth2UserInfo);
//        }
//
//        String email = oAuth2UserInfo.getEmail();
//        String code = oAuth2UserInfo.getCode();
//        log.info("email: {}, code: {}", email, code);
//        Optional<User> optionalUser = userRepository.findBySocialCode(code);
//
//        User user = null;
//        if(optionalUser.isPresent()) {
//            log.info("이미 로그인한 유저 :: 회원가입 완료");
//        } else {
//            user = User.builder()
//                    .loginType(LoginType.KAKAO)
//                    .build();
//
//            userRepository.save(user);
//        }
//
//        return oAuth2User;
    }
}
