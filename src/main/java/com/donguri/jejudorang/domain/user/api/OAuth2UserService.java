package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.response.KakaoUserResponse;
import com.donguri.jejudorang.domain.user.entity.ERole;
import com.donguri.jejudorang.domain.user.entity.LoginType;
import com.donguri.jejudorang.domain.user.entity.Role;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired private final UserRepository userRepository;
    @Autowired private final RoleRepository roleRepository;

    private static final String KAKAO = "kakao";

    public OAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    /*
    * 소셜 로그인 API의 사용자 정보 제공 uri에 요청해 얻은 사용자 정보를 DefaultOAuth2User로 생성해 반환
    *
    * */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2UserService CUSTOM - UserRequest: {}", userRequest);

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId: {}", registrationId); // kakao
        LoginType loginType = convertLoginType(registrationId);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("attributes: {}", attributes);

        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(loginType, userNameAttributeName, attributes);

        User user = getUser(oAuth2Attributes, loginType);
        log.info("user: {}", user);
        log.info("roles: {}", user.getRoles());

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoles().iterator().next().getName().name())),
                attributes, oAuth2Attributes.getAttributeKey());
        log.info("defaultOAuth2User: {}", defaultOAuth2User);
        return defaultOAuth2User;

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

    private User getUser(OAuth2Attributes oAuth2Attributes, LoginType loginType) {
        log.info("getUser()");
        Optional<User> optionalUser = userRepository.findByLoginTypeAndSocialCode(loginType, oAuth2Attributes.getOAuth2UserInfo().getId());
        return optionalUser.orElseGet(() -> saveUser(oAuth2Attributes, loginType));
    }

    private User saveUser(OAuth2Attributes oAuth2Attributes, LoginType loginType) {
        log.info("saveUser()");
        User newUser = oAuth2Attributes.toEntity(loginType, oAuth2Attributes.getOAuth2UserInfo());

        log.info("profile: {}", newUser.getProfile().getExternalId());
        log.info("auth: {}", newUser.getAuth().getAgreement());

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.USER).get());
        newUser.updateRole(roles);

        log.info("roles: {}", roles);

        return userRepository.save(newUser);
    }

    private LoginType convertLoginType(String registrationId) {
        if (KAKAO.equals(registrationId)) {
            return LoginType.KAKAO;
        } else {
            return LoginType.BASIC;
        }
    }

}
