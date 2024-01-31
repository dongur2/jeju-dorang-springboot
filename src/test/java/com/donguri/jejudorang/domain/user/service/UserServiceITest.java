package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.user.dto.MailVerifyRequest;
import com.donguri.jejudorang.domain.user.dto.ProfileRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.AuthenticationRepository;
import com.donguri.jejudorang.domain.user.repository.ProfileRepository;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class UserServiceITest {
    @Autowired EntityManager em;
    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired AuthenticationRepository authenticationRepository;

    @Autowired UserService userService;

    @Value("${mail.test.email}")
    String testmail;


    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void 프로필_수정_성공_이미지_제외() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).phone("01012341234").email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("1234").build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        userRepository.save(user);

        ProfileRequest dataToUpdate = ProfileRequest.builder()
                .email("guriguri@mail.com")
                .nickname("뉴닉네임")
                .build();

        //when
        User nowUser = userRepository.findByExternalId("userId")
                .orElseThrow(() -> new RuntimeException("아이디에 해당하는 유저가 없습니다"));

        nowUser.getAuth().updateEmail(dataToUpdate.email());
        nowUser.getProfile().updateNickname(dataToUpdate.nickname());

        //then
        User dbUser = userRepository.findByExternalId("userId")
                .orElseThrow(() -> new RuntimeException("아이디에 해당하는 유저가 없습니다"));

        Assertions.assertThat(dbUser.getProfile().getNickname()).isEqualTo(dataToUpdate.nickname());
        Assertions.assertThat(dbUser.getAuth().getEmail()).isEqualTo(dataToUpdate.email());
    }

    @Test
    void 이메일_인증_메일_전송() {
        //given
        MailVerifyRequest mailRequest = MailVerifyRequest.builder()
                .email(testmail)
                .build();

        //when, then
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> userService.checkMail(mailRequest));
    }

    @Test
    void 오류_이메일_인증_공백() {
        //given
        MailVerifyRequest mailRequest = MailVerifyRequest.builder()
                .email(null)
                .build();

        //when, then
        assertThrows(Exception.class, () -> userService.checkMail(mailRequest));
    }

    @Test
    void 오류_이메일_인증_중복() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email(testmail).agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("1234").build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        userRepository.save(user);

        //when
        MailVerifyRequest mailRequest = MailVerifyRequest.builder()
                .email(testmail)
                .build();

        //then
        assertThrows(Exception.class, () -> userService.checkMail(mailRequest));
    }

}