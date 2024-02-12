package com.donguri.jejudorang.domain.community.dto.request;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;


@SpringBootTest
class CommunityWriteRequestDtoTest {

    @Autowired EntityManager em;
    @Autowired Validator validator;

    @AfterEach
    void after() {
        em.clear();
    }

    @Test
    void DTO_실패_제목_누락() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("제목을 작성해주세요.");
    }

    @Test
    void DTO_실패_제목_글자수_부족() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("가")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("제목은 2자 이상 60자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_제목_글자수_초과() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("일이삼사오륙칠팔구십일이삼사오륙칠팔구십일이삼사오륙칠팔구십일이삼사오륙칠팔구십일이삼사오륙칠팔구십일이삼사오륙칠팔구십일이삼사오륙칠팔구십")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("제목은 2자 이상 60자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_글_제목_공백() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("               ")
                .type("chat")
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("제목을 작성해주세요.");
    }

    @Test
    void DTO_실패_타입_누락() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("글 작성 테스트 제목")
                .type(null)
                .content("커뮤니티 글작성 테스트 - CHAT")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("Error");
    }

    @Test
    void DTO_실패_글_내용_글자수_부족() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("글 작성 테스트 제목")
                .type("chat")
                .content("n")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("글 내용은 2자 이상 4000자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_글_내용_누락() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("글 작성 테스트 제목")
                .type("chat")
                .content(null)
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("글 내용을 작성해주세요.");
    }

    @Test
    void DTO_실패_글_내용_공백() {
        // given
        CommunityWriteRequest postToWrite = CommunityWriteRequest.builder()
                .title("글 작성 테스트 제목")
                .type("chat")
                .content("                   ")
                .build();

        //when
        Set<ConstraintViolation<CommunityWriteRequest>> validate = validator.validate(postToWrite);

        //then
        Iterator<ConstraintViolation<CommunityWriteRequest>> iterator = validate.iterator();
        List<String> msgList = new ArrayList<>();

        while(iterator.hasNext()) {
            String message = iterator.next().getMessage();
            msgList.add(message);
        }
        Assertions.assertThat(msgList).contains("글 내용을 작성해주세요.");
    }

}