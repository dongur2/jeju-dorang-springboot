package com.donguri.jejudorang.domain.user.dto;

import com.donguri.jejudorang.domain.user.dto.request.SignUpRequest;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@SpringBootTest
class SignUpRequestTest {

    @Value("${mail.test.email}") String testmail;

    @Autowired EntityManager em;
    @Autowired Validator validator;


    @Test
    void DTO_실패_이메일_누락() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId1212")
                .nickname("nickname")
                .emailToSend(null)
                .isVerified(false)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("이메일을 작성해주세요.", "이메일 인증이 필요합니다.");
    }

    @Test
    void DTO_실패_이메일_인증_미완료() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId12")
                .nickname("nickname")
                .emailToSend("email") // eamil@ error , email@w none-error
                .isVerified(false)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("이메일 인증이 필요합니다.");
    }

    @Test
    void DTO_실패_이메일_형식_미충족() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId12")
                .nickname("nickname")
                .emailToSend("email") // eamil@ error , email@w none-error
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("이메일 형식으로 작성해주세요.");
    }

    @Test
    void DTO_실패_아이디_누락() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId(null)
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("아이디를 작성해주세요.");
    }

    @Test
    void DTO_실패_아이디_글자수_부족() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("아이디는 5자 이상 20자 이하의 영문자와 숫자 조합만 가능합니다.");
    }

    @Test
    void DTO_실패_아이디_글자수_초과() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("usernameisusernameisusernameWHAT")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("아이디는 5자 이상 20자 이하의 영문자와 숫자 조합만 가능합니다.");
    }

    @Test
    void DTO_실패_아이디_특수문자_포함() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user!!")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("아이디는 5자 이상 20자 이하의 영문자와 숫자 조합만 가능합니다.");
    }

    @Test
    void DTO_실패_아이디_한글_포함() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("use휴ㅋㅋ")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("아이디는 5자 이상 20자 이하의 영문자와 숫자 조합만 가능합니다.");
    }

    @Test
    void DTO_성공_닉네임() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user1212")
                .nickname("간zIkings2")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
    void DTO_실패_닉네임_특수문자_포함() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user1212")
                .nickname("nickname!")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("닉네임은 공백을 포함하는 특수문자, 이모티콘을 제외한 2자 이상 15자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_닉네임_이모티콘_포함() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user1212")
                .nickname("nickname🧸")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("닉네임은 공백을 포함하는 특수문자, 이모티콘을 제외한 2자 이상 15자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_닉네임_글자수_부족() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user1212")
                .nickname("n")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("닉네임은 2자 이상 15자 이하만 가능합니다.", "닉네임은 공백을 포함하는 특수문자, 이모티콘을 제외한 2자 이상 15자 이하만 가능합니다.", "닉네임은 2자 이상 15자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_닉네임_글자수_초과() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user1212")
                .nickname("넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘넘")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("닉네임은 2자 이상 15자 이하만 가능합니다.", "닉네임은 공백을 포함하는 특수문자, 이모티콘을 제외한 2자 이상 15자 이하만 가능합니다.", "닉네임은 2자 이상 15자 이하만 가능합니다.");
    }

    @Test
    void DTO_실패_닉네임_누락() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("user1212")
                .nickname(null)
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("닉네임을 작성해주세요.");
    }




    @Test
    void DTO_성공_비밀번호_충족() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId12")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
    void DTO_실패_비밀번호_누락() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId12")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password(null)
                .passwordForCheck(null)
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        org.assertj.core.api.Assertions.assertThat(msgList).contains("비밀번호를 작성해주세요.", "비밀번호를 한 번 더 입력해주세요.");
    }

    @Test
    void DTO_실패_비밀번호_확인_누락() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId12")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck(null)
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
                .externalId("userId12")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("123!w")
                .passwordForCheck("123!w")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("123!wwaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .passwordForCheck("123!wwaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("12345678910")
                .passwordForCheck("12345678910")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("aaaaaaaaaaaaa")
                .passwordForCheck("aaaaaaaaaaaaa")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("!!!!!!!!!!")
                .passwordForCheck("!!!!!!!!!!")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("!!!!!!!!aa")
                .passwordForCheck("!!!!!!!!aa")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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
    void DTO_성공_약관동의_모두() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(true)
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

        Assertions.assertThat(msgList).isEmpty();
    }

    @Test
    void DTO_성공_약관동의_선택() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(false)
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

        Assertions.assertThat(msgList).isEmpty();
    }

    @Test
    void DTO_성공_약관동의_필수_이용약관_미동의() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(false)
                .agreeForPrivateNecessary(true)
                .agreeForPrivateOptional(true)
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

        Assertions.assertThat(msgList).contains("제주도랑 이용약관에 동의해야 가입이 가능합니다.");
    }

    @Test
    void DTO_성공_약관동의_필수_개인정보_미동의() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(true)
                .agreeForPrivateNecessary(false)
                .agreeForPrivateOptional(false)
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

        Assertions.assertThat(msgList).contains("필수 개인정보 수집 및 이용에 동의해야 가입이 가능합니다.");
    }

    @Test
    void DTO_성공_약관동의_필수_미동의_선택_동의() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(false)
                .agreeForPrivateNecessary(false)
                .agreeForPrivateOptional(true)
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

        Assertions.assertThat(msgList).contains("제주도랑 이용약관에 동의해야 가입이 가능합니다.", "필수 개인정보 수집 및 이용에 동의해야 가입이 가능합니다.");
    }

    @Test
    void DTO_성공_약관동의_모두_미동의() {
        //given
        SignUpRequest request = SignUpRequest.builder()
                .externalId("userId")
                .nickname("nickname")
                .emailToSend("email@mail.com")
                .isVerified(true)
                .password("abcde@@123")
                .passwordForCheck("abcde@@123")
                .agreeForUsage(false)
                .agreeForPrivateNecessary(false)
                .agreeForPrivateOptional(false)
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

        Assertions.assertThat(msgList).contains("제주도랑 이용약관에 동의해야 가입이 가능합니다.", "필수 개인정보 수집 및 이용에 동의해야 가입이 가능합니다.");
    }

}