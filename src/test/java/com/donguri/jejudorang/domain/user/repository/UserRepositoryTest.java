package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.auth.PasswordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    /*
    * EntityManager
    * JPA에서 엔터티 매니징을 담당하는 인터페이스 - 영속성 컨텍스트를 제어하고 엔터티의 생명주기를 관리
    *
    * > clear(): 영속성 컨텍스트의 모든 엔터티를 분리(detach)하고, 영속성 컨텍스트를 초기화
    *
    * 각각의 테스트가 독립적으로 실행되도록 보장하여 테스트 간의 영향을 최소화
    *
    * */
    @Autowired EntityManager em;
    @Autowired UserRepository userRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired AuthenticationRepository authenticationRepository;
    @Autowired PasswordRepository passwordRepository;
    @Autowired RoleRepository roleRepository;

    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void 회원가입_성공() {
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

        //when
        //cascade = CascadeType.PERSIST -> 영속성 전이로 부모 저장하면 자식까지 저장
        User savedUser = userRepository.save(user);

        //then
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));
        assertThat(foundUser).isEqualTo(savedUser);

        Profile foundProf = profileRepository.findByUser(foundUser)
                .orElseThrow(() -> new RuntimeException("저장된 프로필이 없습니다"));
        assertThat(foundProf).isEqualTo(savedUser.getProfile());

        Authentication foundAuth = authenticationRepository.findByUser(foundUser)
                .orElseThrow(() -> new RuntimeException("저장된 인증이 없습니다"));
        assertThat(foundAuth).isEqualTo(savedUser.getAuth());

        Password foundPwd = passwordRepository.findByUser(foundUser)
                .orElseThrow(() -> new RuntimeException("저장된 비밀번호가 없습니다"));
        assertThat(foundPwd).isEqualTo(savedUser.getPwd());
    }

    @Test
    void 오류_아이디_없음() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).nickname("usernickname").build();
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

        //when - then
        Assertions.assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    void 오류_닉네임_없음() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").build();
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

        //when - then
        Assertions.assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    void 오류_이메일_없음() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).phone("01012341234").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).password("1234").build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        //when - then
        Assertions.assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    void 오류_동의항목_비동의() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).phone("01012341234").email("user@mail.com").build();
        Password password = Password.builder().user(user).password("1234").build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        //when - then
        Assertions.assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    void 오류_비밀번호_없음() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).phone("01012341234").email("user@mail.com").agreement(AgreeRange.ALL).build();
        Password password = Password.builder().user(user).build();

        Set<Role> testRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
        testRoles.add(userRole);

        user.updateRole(testRoles);
        user.updateProfile(profile);
        user.updateAuth(authentication);
        user.updatePwd(password);

        //when - then
        Assertions.assertThrows(Exception.class, () -> userRepository.save(user));
    }


    @Test
    void 회원정보_수정() {
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

        User savedUser = userRepository.save(user);

        //when
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저가 없습니다"));

        foundUser.getProfile().updateNickname("updateNickname");
        foundUser.getProfile().updateImgUrl("imgurl.com");
        foundUser.getAuth().updateEmail("new@mail.com");
        foundUser.getAuth().updatePhone("01099999999");

        //then
        User updatedUser = userRepository.findById(foundUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저가 없습니다"));
        Profile updatedProf = profileRepository.findByUser(foundUser)
                .orElseThrow(() -> new EntityNotFoundException("유저에 해당하는 프로필이 없습니다"));
        Authentication updatedAuth = authenticationRepository.findByUser(foundUser)
                .orElseThrow(() -> new RuntimeException("유저에 해당하는 인증이 없습니다"));

        assertThat(updatedProf.getNickname()).isEqualTo("updateNickname");
        assertThat(updatedProf.getImgUrl()).isEqualTo("imgurl.com");
        assertThat(updatedAuth.getEmail()).isEqualTo("new@mail.com");
        assertThat(updatedAuth.getPhone()).isEqualTo("01099999999");
    }

    @Test
    void 비밀번호_수정() {
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

        User savedUser = userRepository.save(user);

        //when
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저가 없습니다"));

        /*
        * BCryptPasswordEncoder
        * Spring Security에서 제공하는 비밀번호 암호화(해싱)를 위한 클래스
        * > BCrypt: 단방향 해시 함수
        *
        * */
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        foundUser.getPwd().updatePassword(passwordEncoder, "updatePwd1234");

        Password foundPwd = passwordRepository.findByUser(foundUser)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 비밀번호가 없습니다"));

        //then
        assertThat(passwordEncoder.matches("updatePwd1234", foundPwd.getPassword())).isTrue();
    }

    @Test
    void 회원삭제() {
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

        User savedUser = userRepository.save(user);

        //when
        userRepository.delete(savedUser);

        Assertions.assertThrows(Exception.class,
                () -> userRepository.findById(savedUser.getId())
                        .orElseThrow(() -> new EntityNotFoundException("삭제된 유저입니다")));

        Assertions.assertThrows(Exception.class,
                () -> profileRepository.findByUser(savedUser)
                        .orElseThrow(() -> new EntityNotFoundException("삭제된 프로필입니다")));
        Assertions.assertThrows(Exception.class,
                () -> authenticationRepository.findByUser(savedUser)
                        .orElseThrow(() -> new EntityNotFoundException("삭제된 인증입니다")));
        Assertions.assertThrows(Exception.class,
                () -> passwordRepository.findByUser(savedUser)
                        .orElseThrow(() -> new EntityNotFoundException("삭제된 비밀번호입니다")));

    }
}