package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.MailVerifyRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.AgreeRange;
import com.donguri.jejudorang.domain.user.entity.User;
import com.donguri.jejudorang.domain.user.repository.UserRepository;
import com.donguri.jejudorang.domain.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


@SpringBootTest
class UserControllerTest {
    @Value("${mail.test.email}") String testmail;

    @Autowired EntityManager em;
    @Autowired Validator validator;

    @Autowired UserController userController;

    @Autowired UserService userService;
    @Autowired RedisTemplate<String, String> redisTemplate;

    @Autowired UserRepository userRepository;

    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void 회원_가입_성공_모든_항목_동의() {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .externalId("dongur2")
                .nickname("닉네임isS2")
                .emailToSend(testmail)
                .isVerified(true)
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(true)
                .password("abcde!1234")
                .passwordForCheck("abcde!1234")
                .build();

        //when
        userService.signUp(signUpRequest);

        //then
        User dbUser = userRepository.findByExternalId("dongur2")
                .orElseThrow(() -> new EntityNotFoundException("아이디에 해당하는 유저가 없습니다."));

        org.assertj.core.api.Assertions.assertThat(dbUser.getAuth().getAgreement())
                .isEqualTo(AgreeRange.ALL);

        userRepository.delete(dbUser);
    }

    @Test
    void 회원_가입_성공_필수_항목_동의() {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .externalId("dongur2")
                .nickname("닉네임isS2")
                .emailToSend(testmail)
                .isVerified(true)
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
                .password("abcde!1234")
                .passwordForCheck("abcde!1234")
                .build();

        //when
        userService.signUp(signUpRequest);

        //then
        User dbUser = userRepository.findByExternalId("dongur2")
                .orElseThrow(() -> new EntityNotFoundException("아이디에 해당하는 유저가 없습니다."));

        org.assertj.core.api.Assertions.assertThat(dbUser.getAuth().getAgreement())
                .isEqualTo(AgreeRange.NECESSARY);

        userRepository.delete(dbUser);
    }

    @Test
    void 회원_가입_실패_필수_항목_미동의() {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .externalId("dongur2")
                .nickname("닉네임isS2")
                .emailToSend(testmail)
                .isVerified(true)
                .agreeForUsage(true)
                .agreeForPrivateNecessary(false)
                .agreeForPrivateOptional(false)
                .password("abcde!1234")
                .passwordForCheck("abcde!1234")
                .build();

        //when
        Assertions.assertThrows(RuntimeException.class, () -> userService.signUp(signUpRequest));
        org.assertj.core.api.Assertions.assertThat(userRepository.findByExternalId("dongur2")).isEmpty();
    }

    @Test
    void 회원_가입_실패_동의_항목_모두_미동의() {
        //given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .externalId("dongur2")
                .nickname("닉네임isS2")
                .emailToSend(testmail)
                .isVerified(true)
                .agreeForUsage(false)
                .agreeForPrivateNecessary(false)
                .agreeForPrivateOptional(false)
                .password("abcde!1234")
                .passwordForCheck("abcde!1234")
                .build();

        //when
        Assertions.assertThrows(RuntimeException.class, () -> userService.signUp(signUpRequest));
        org.assertj.core.api.Assertions.assertThat(userRepository.findByExternalId("dongur2")).isEmpty();
    }



    @Test
    void 이메일_인증_번호_불일치() {
        //given
        String testCode = "900900";
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(testmail, testCode);

        MailVerifyRequest request = MailVerifyRequest.builder().email(testmail).code("900901").build();

        //when, then
        Assertions.assertDoesNotThrow(() -> userController.checkEmailCode(request, null));
    }

}