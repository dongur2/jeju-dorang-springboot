package com.donguri.jejudorang.domain.user.repository;

import com.donguri.jejudorang.domain.user.entity.*;
import com.donguri.jejudorang.domain.user.entity.auth.Password;
import com.donguri.jejudorang.domain.user.repository.auth.PasswordRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void 회원가입_성공() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).role(Role.USER).build();
        Profile profile = Profile.builder().user(user).externalId("userId").nickname("userNickname").build();
        Authentication authentication = Authentication.builder().user(user).phone("01012341234").email("user@mail.com").agreement((byte) 1).build();
        Password password = Password.builder().user(user).password("1234").build();

        //when
        User savedUser = userRepository.save(user);
        Profile savedProf = profileRepository.save(profile);
        Authentication savedAuth = authenticationRepository.save(authentication);
        Password savedPwd = passwordRepository.save(password);

        //then
        User foundUser = userRepository.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다."));
        assertThat(foundUser).isEqualTo(savedUser);

        Profile foundProf = profileRepository.findByUser(savedUser)
                .orElseThrow(() -> new RuntimeException("저장된 프로필이 없습니다"));
        assertThat(foundProf).isEqualTo(savedProf);

        Authentication foundAuth = authenticationRepository.findByUser(savedUser)
                .orElseThrow(() -> new RuntimeException("저장된 인증이 없습니다"));
        assertThat(foundAuth).isEqualTo(savedAuth);

        Password foundPwd = passwordRepository.findByUser(savedUser)
                .orElseThrow(() -> new RuntimeException("저장된 비밀번호가 없습니다"));
        assertThat(foundPwd).isEqualTo(savedPwd);
    }

    @Test
    void 오류_아이디_없음() {
        //given
        User user = User.builder().loginType(LoginType.BASIC).role(Role.USER).build();
        Profile profile = Profile.builder().user(user).nickname("usernickname").build();
        Authentication authentication = new Authentication(user, "01012341234", "user@mail.com", (byte) 1);
        Password password = new Password(user, "1234");

        //when - then
        User savedUser = userRepository.save(user);
        Assertions.assertThrows(Exception.class, () -> profileRepository.save(profile));
        Authentication savedAuth = authenticationRepository.save(authentication);
        Password savedPwd = passwordRepository.save(password);
    }



}