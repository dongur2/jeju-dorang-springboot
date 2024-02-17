package com.donguri.jejudorang.domain.user.service;

import com.donguri.jejudorang.domain.community.dto.request.CommunityWriteRequest;
import com.donguri.jejudorang.domain.community.repository.CommunityRepository;
import com.donguri.jejudorang.domain.community.service.CommunityService;
import com.donguri.jejudorang.domain.user.dto.request.email.MailChangeRequest;
import com.donguri.jejudorang.domain.user.dto.request.PasswordRequest;
import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Authentication;
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



@SpringBootTest
class UserServiceITest {
    @Autowired EntityManager em;

    @Autowired RoleRepository roleRepository;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired PasswordRepository passwordRepository;
    @Autowired AuthenticationRepository authenticationRepository;

    @Autowired CommunityRepository communityRepository;

    @Autowired UserService userService;
    @Autowired CommunityService communityService;

    @Autowired PasswordEncoder passwordEncoder;

    @Value("${aws.s3.default-img.name}") private String defaultImgName;
    @Value("${aws.s3.default-img.url}") private String defaultImgUrl;

    @AfterEach
    void after() {
        em.clear();
        communityRepository.flush();
        userRepository.flush();
        profileRepository.flush();
        passwordRepository.flush();
        authenticationRepository.flush();
    }

    @Test
    void 프로필_닉네임_변경() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

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
        savedUser.getProfile().updateNickname("수정한닉네임");

        //then
        Assertions.assertThat(savedUser.getProfile().getNickname()).isEqualTo("수정한닉네임");
    }

    @Test
    @Transactional
    void 비밀번호_수정_성공() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

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
        PasswordRequest pwdToUpdate = PasswordRequest.builder()
                .oldPwd("12345678aa!w").newPwd("newnewS2~!").newPwdToCheck("newnewS2~!").build();

        Assertions.assertThat(passwordEncoder.matches(pwdToUpdate.oldPwd(), savedUser.getPwd().getPassword())).isTrue();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> pwdToUpdate.newPwd().equals(pwdToUpdate.newPwdToCheck()));

        savedUser.getPwd().updatePassword(passwordEncoder, pwdToUpdate.newPwd());

        //thenX
        String password1 = savedUser.getPwd().getPassword();
        Assertions.assertThat(passwordEncoder.matches(pwdToUpdate.newPwd(), password1)).isTrue();
    }

    @Test
    @Transactional
    void 비밀번호_수정_현재_비번_불일치() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

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
        PasswordRequest pwdToUpdate = PasswordRequest.builder()
                .oldPwd("12345678aa!ww").newPwd("newnewS2~!").newPwdToCheck("newnewS2~!").build();

        //then
        Assertions.assertThat(passwordEncoder.matches(pwdToUpdate.oldPwd(), savedUser.getPwd().getPassword())).isFalse();
    }

    @Test
    @Transactional
    void 비밀번호_수정_새_비번_불일치() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

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
        PasswordRequest pwdToUpdate = PasswordRequest.builder().oldPwd("12345678aa!w").newPwd("newnewS2~!").newPwdToCheck("newnewS22~!").build();

        //then
        Assertions.assertThat(pwdToUpdate.newPwd().equals(pwdToUpdate.newPwdToCheck())).isFalse();
    }

    @Test
    @Transactional
    void 이메일_변경_성공() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

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
                .emailToSend("user2@mail.com").isVerified(true).build();

        savedUser.getAuth().updateEmail(request.emailToSend());

        //then
        Assertions.assertThat(savedUser.getAuth().getEmail()).isEqualTo(request.emailToSend());
    }

    @Test
    @Transactional
    void 회원_탈퇴() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        Long savedUser = userRepository.save(user).getId();

        //when
        userRepository.deleteById(savedUser);

        //then
        Assertions.assertThat(userRepository.findById(savedUser)).isEmpty();
    }

    @Test
    @Transactional
    void 회원_탈퇴_작성글_개수_그대로_확인() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").imgName(defaultImgName).imgUrl(defaultImgUrl).build();
        Authentication authentication = Authentication.builder().user(user).email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password(passwordEncoder.encode("12345678aa!w")).build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        //when
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("커뮤니티 글작성 테스트 제목")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        Long community = communityRepository.save(postToWrite.toEntity(savedUser)).getId();

        //when
        communityService.findAllPostsByUserAndSetWriterNull(userId);
        userRepository.deleteById(userId);

        //then
        Assertions.assertThat(communityRepository.findById(community)).isPresent();
        Assertions.assertThat(userRepository.findById(userId)).isEmpty();
    }
}