package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import com.donguri.jejudorang.domain.user.dto.request.email.MailSendRequest;
import com.donguri.jejudorang.domain.user.dto.request.PasswordRequest;
import com.donguri.jejudorang.domain.user.dto.request.ProfileRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.AuthenticationRepository;
import com.donguri.jejudorang.domain.user.repository.ProfileRepository;
import com.donguri.jejudorang.domain.user.repository.RoleRepository;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.domain.user.repository.auth.PasswordRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired PasswordRepository passwordRepository;
    @Autowired AuthenticationRepository authenticationRepository;

    @Autowired UserService userService;

    @Autowired PasswordEncoder passwordEncoder;

    @Value("${mail.test.email}")
    String testMail;
    @Value(("${mail.test.new-email}"))
    String testMail2;


    @AfterEach
    void after() {
        em.clear();
        userRepository.flush();
        profileRepository.flush();
        passwordRepository.flush();
        authenticationRepository.flush();
    }

    @Test
    void 프로필_수정_성공_이미지_제외() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
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
                .nickname("뉴닉네임")
                .build();

        //when
        User nowUser = userRepository.findByExternalId("userId")
                .orElseThrow(() -> new RuntimeException("아이디에 해당하는 유저가 없습니다"));

        nowUser.getProfile().updateNickname(dataToUpdate.nickname());

        //then
        User dbUser = userRepository.findByExternalId("userId")
                .orElseThrow(() -> new RuntimeException("아이디에 해당하는 유저가 없습니다"));

        Assertions.assertThat(dbUser.getProfile().getNickname()).isEqualTo(dataToUpdate.nickname());
    }

    @Test
    void 이메일_인증_메일_전송() {
        //given
        MailSendRequest mailRequest = MailSendRequest.builder()
                .email(testMail)
                .build();

        //when, then
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> userService.sendVerifyMail(mailRequest));
    }

    @Test
    void 오류_이메일_인증_공백() {
        //given
        MailSendRequest mailRequest = MailSendRequest.builder()
                .email(null)
                .build();

        //when, then
        assertThrows(Exception.class, () -> userService.sendVerifyMail(mailRequest));
    }

    @Test
    void 오류_이메일_인증_중복() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email(testMail).agreement(AgreeRange.ALL).build();
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
        MailSendRequest mailRequest = MailSendRequest.builder()
                .email(testMail)
                .build();

        //then
        assertThrows(Exception.class, () -> userService.sendVerifyMail(mailRequest));
    }

    @Test
    void 비밀번호_수정_성공() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email(testMail).agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("abcde!!1234").build();
        password.updatePassword(passwordEncoder, password.getPassword());

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        User savedUser = userRepository.save(user);
        Long id = savedUser.getId();

        //when
        PasswordRequest pwdToUpdate = PasswordRequest.builder()
                .oldPwd("abcde!!1234").newPwd("newnewS2~!").newPwdToCheck("newnewS2~!").build();

        //then
        Assertions.assertThat(passwordEncoder.matches(pwdToUpdate.oldPwd(), savedUser.getPwd().getPassword())).isTrue();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> pwdToUpdate.newPwd().equals(pwdToUpdate.newPwdToCheck()));

        savedUser.getPwd().updatePassword(passwordEncoder, pwdToUpdate.newPwd());

        String password1 = userRepository.findById(id).get().getPwd().getPassword();
        Assertions.assertThat(passwordEncoder.matches(pwdToUpdate.newPwd(), password1)).isTrue();
    }

    @Test
    void 비밀번호_수정_현재_비번_불일치() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email(testMail).agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("abcde!!1234").build();
        password.updatePassword(passwordEncoder, password.getPassword());

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        User savedUser = userRepository.save(user);

        //when
        PasswordRequest pwdToUpdate = PasswordRequest.builder().oldPwd("abcde!!12345").newPwd("newnewS2~!").newPwdToCheck("newnewS2~!").build();

        //then
        Assertions.assertThat(passwordEncoder.matches(pwdToUpdate.oldPwd(), savedUser.getPwd().getPassword())).isFalse();
    }

    @Test
    void 비밀번호_수정_새_비번_불일치() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email(testMail).agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("abcde!!1234").build();
        password.updatePassword(passwordEncoder, password.getPassword());

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        User savedUser = userRepository.save(user);

        //when
        PasswordRequest pwdToUpdate = PasswordRequest.builder().oldPwd("abcde!!12345").newPwd("newnewS2~!").newPwdToCheck("newnewS22~!").build();

        //then
        Assertions.assertThat(pwdToUpdate.newPwd().equals(pwdToUpdate.newPwdToCheck())).isFalse();
    }

    @Test
    void 이메일_변경_성공() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).email(testMail).agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("abcde!!1234").build();
        password.updatePassword(passwordEncoder, password.getPassword());

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        User savedUser = userRepository.save(user);

        //when
        MailChangeRequest request = MailChangeRequest.builder()
                .emailToSend(testMail2).isVerified(true).build();

        savedUser.getAuth().updateEmail(request.emailToSend());

        //then
        Assertions.assertThat(userRepository.findById(savedUser.getId()).get().getAuth().getEmail())
                .isEqualTo(request.emailToSend());
    }

}