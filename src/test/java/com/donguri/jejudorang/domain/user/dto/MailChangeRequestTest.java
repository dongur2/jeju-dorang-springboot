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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailChangeRequestTest {
    @Autowired
    EntityManager em;
    @Autowired
    Validator validator;

    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void DTO_생성() {
        //given
        MailChangeRequest request = MailChangeRequest.builder()
                .emailToSend("dong@email.com").isVerified(true).build();

        //when
        Set<ConstraintViolation<MailChangeRequest>> validate = validator.validate(request);

        Iterator<ConstraintViolation<MailChangeRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).isEmpty();
    }

    @Test
    void DTO_생성_실패_이메일_누락() {
        //given
        MailChangeRequest request = MailChangeRequest.builder()
                .emailToSend(" ").isVerified(true).build();

        //when
        Set<ConstraintViolation<MailChangeRequest>> validate = validator.validate(request);

        Iterator<ConstraintViolation<MailChangeRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).contains("이메일을 작성해주세요.");
    }

    @Test
    void DTO_생성_인증_FALSE() {
        //given
        MailChangeRequest request = MailChangeRequest.builder()
                .emailToSend("dong@email.com").isVerified(false).build();

        //when
        Set<ConstraintViolation<MailChangeRequest>> validate = validator.validate(request);

        Iterator<ConstraintViolation<MailChangeRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }

        //then
        Assertions.assertThat(msgList).contains("이메일 인증이 필요합니다.");
    }

}