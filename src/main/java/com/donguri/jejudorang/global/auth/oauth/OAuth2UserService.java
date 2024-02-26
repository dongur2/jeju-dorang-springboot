package com.donguri.jejudorang.global.auth.oauth;

import com.donguri.jejudorang.domain.user.entity.ERole;
import com.donguri.jejudorang.domain.user.entity.LoginType;
import com.donguri.jejudorang.domain.user.entity.Role;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    private final String defaultImgName;
    private final String defaultImgUrl;

    public OAuth2UserService(UserRepository userRepository, RoleRepository roleRepository,
                             @Value("${aws.s3.default-img.name}") String defaultImgName,
                             @Value("${aws.s3.default-img.url}") String defaultImgUrl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.defaultImgName = defaultImgName;
        this.defaultImgUrl = defaultImgUrl;
    }


    /*
    * OAuth 2.0 프로토콜에 따라 로그인 요청이 들어왔을 때 호출
    * 소셜 로그인 API의 사용자 정보 제공 uri에 요청해 얻은 사용자 정보를 DefaultOAuth2User로 생성해 반환
    *
    * */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2UserService CUSTOM - UserRequest: {}", userRequest);

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 로그인 타입 설정
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // kakao
        LoginType loginType = convertLoginType(registrationId);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // id

        // Resource Server에서 불러온 사용자 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 로그인한 소셜서비스에 맞게 유저 정보 OAuth2Attributes로 변환
        OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(loginType, userNameAttributeName, attributes);

        // * 이미 가입된 회원일 경우 DB에서 찾아 반환하고, 아니라면 DB에 저장 후 반환
        User user = getUser(oAuth2Attributes, loginType);

        // DefaultOAuth2User 반환: 인증된 사용자 - 권한, 속성 포함 -> Security 인증 및 권한 부여에 사용
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoles().iterator().next().getName().name())),
                attributes, oAuth2Attributes.getAttributeKey());
    }

    // DB 회원 조회
    private User getUser(OAuth2Attributes oAuth2Attributes, LoginType loginType) {
        Optional<User> optionalUser = userRepository.findByLoginTypeAndSocialCode(loginType, oAuth2Attributes.getOAuth2UserInfo().getId());
        return optionalUser.orElseGet(() -> saveUser(oAuth2Attributes, loginType));
    }

    // DB에 저장
    private User saveUser(OAuth2Attributes oAuth2Attributes, LoginType loginType) {
        User newUser = oAuth2Attributes.toEntity(loginType, oAuth2Attributes.getOAuth2UserInfo());

        // 기본 프로필 사진 설정
        newUser.getProfile().updateImg(defaultImgName, defaultImgUrl);

        // 권한 설정해서 저장
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.USER).get());
        newUser.updateRole(roles);

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
