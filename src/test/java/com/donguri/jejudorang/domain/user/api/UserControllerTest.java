package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.MailVerifyRequest;
import com.donguri.jejudorang.domain.user.dto.SignUpRequest;
import com.donguri.jejudorang.domain.user.entity.AgreeRange;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SpringBootTest
class UserControllerTest {
    @Value("${mail.test.email}") String testmail;

    @Autowired EntityManager em;
    @Autowired Validator validator;

    @Autowired UserController userController;
    @Autowired RedisTemplate<String, String> redisTemplate;


    @AfterEach
    void after() {
        em.clear();
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

    @Test
    void DTO_성공_비밀번호_충족() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).isEmpty();
    }

    @Test
    void DTO_실패_비밀번호_확인_미작성() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("abcde@@123")
                .passwordForCheck(null)
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호를 한 번 더 입력해주세요.");
    }

    @Test
    void DTO_실패_비밀번호_글자수_부족() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("123!w")
                .passwordForCheck("123!w")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_비밀번호_글자수_초과() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("123!wwaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .passwordForCheck("123!wwaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_비밀번호_정규식_미충족_숫자만() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("12345678910")
                .passwordForCheck("12345678910")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_비밀번호_정규식_미충족_문자만() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("aaaaaaaaaaaaa")
                .passwordForCheck("aaaaaaaaaaaaa")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_비밀번호_정규식_미충족_특수문자만() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("!!!!!!!!!!")
                .passwordForCheck("!!!!!!!!!!")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_비밀번호_정규식_미충족_특수문자와_문자만() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .email("email@mail.com")
                .password("!!!!!!!!aa")
                .passwordForCheck("!!!!!!!!aa")
                .build();

        //when
        Set<ConstraintViolation<SignUpRequest>> validate = validator.validate(request);

        //then
        Iterator<ConstraintViolation<SignUpRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호는 특수 문자와 숫자가 적어도 하나가 포함된 8자 이상 20자 이하만 가능합니다.");
    }


}