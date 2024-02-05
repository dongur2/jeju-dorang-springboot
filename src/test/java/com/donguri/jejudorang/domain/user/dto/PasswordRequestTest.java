package com.donguri.jejudorang.domain.user.dto;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@SpringBootTest
class PasswordRequestTest {

    @Autowired EntityManager em;
    @Autowired Validator validator;

    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void 비밀번호_요청_DTO_성공() {
        //given
        PasswordRequest passwordRequest = PasswordRequest.builder()
                .oldPwd("abcde~!zzz").newPwd("ABCDEFG!!$$").newPwdToCheck("ABCDEFG!!$$").build();

        //when
        Set<ConstraintViolation<PasswordRequest>> validate = validator.validate(passwordRequest);

        Iterator<ConstraintViolation<PasswordRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).isEmpty();
    }

    @Test
    void 비밀번호_요청_DTO_현재_비밀번호_누락() {
        //given
        PasswordRequest passwordRequest = PasswordRequest.builder()
                .oldPwd(" ").newPwd("ABCDEFG!!$$").newPwdToCheck("ABCDEFG!!$$").build();

        //when
        Set<ConstraintViolation<PasswordRequest>> validate = validator.validate(passwordRequest);

        Iterator<ConstraintViolation<PasswordRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).contains("현재 비밀번호를 입력해주세요.");
    }

    @Test
    void 비밀번호_요청_DTO_새_비밀번호_누락() {
        //given
        PasswordRequest passwordRequest = PasswordRequest.builder()
                .oldPwd("abcde~!zzz").newPwd(" ").newPwdToCheck("ABCDEFG!!$$").build();

        //when
        Set<ConstraintViolation<PasswordRequest>> validate = validator.validate(passwordRequest);

        Iterator<ConstraintViolation<PasswordRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).contains("새 비밀번호를 입력해주세요.");
    }

    @Test
    void 비밀번호_요청_DTO_새_비밀번호_확인_누락() {
        //given
        PasswordRequest passwordRequest = PasswordRequest.builder()
                .oldPwd("abcde~!zzz").newPwd("ABCDEFG!!$$").newPwdToCheck(" ").build();

        //when
        Set<ConstraintViolation<PasswordRequest>> validate = validator.validate(passwordRequest);

        Iterator<ConstraintViolation<PasswordRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).contains("새 비밀번호를 한 번 더 입력해주세요.");
    }

    @Test
    void 비밀번호_요청_DTO_새_비밀번호_전체_누락() {
        //given
        PasswordRequest passwordRequest = PasswordRequest.builder()
                .oldPwd("abcde~!zzz").newPwd(" ").newPwdToCheck(" ").build();

        //when
        Set<ConstraintViolation<PasswordRequest>> validate = validator.validate(passwordRequest);

        Iterator<ConstraintViolation<PasswordRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).contains("새 비밀번호를 입력해주세요.");
    }
}