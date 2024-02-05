package com.donguri.jejudorang.domain.user.service.auth;

import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


@SpringBootTest
class MailServiceITest {
    @Autowired EntityManager em;
    @Autowired RedisTemplate<String, String> redisTemplate;

    @Autowired MailService mailService;

    @Value("${mail.test.email}") String testmail;


    @AfterEach
    void after() {
        em.clear();
    }


    @Test
    void 인증_코드_일치() {
        //given
        String testCode = "900900";
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(testmail, testCode);

        MailVerifyRequest request = MailVerifyRequest.builder().email(testmail).code("900900").build();

        //when
        org.assertj.core.api.Assertions.assertThat(mailService.checkAuthMail(request)).isTrue();
    }

    @Test
    void 인증_코드_불일치() {
        //given
        String testCode = "900900";
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(testmail, testCode);

        MailVerifyRequest request = MailVerifyRequest.builder().email(testmail).code("900901").build();

        //when, then
        org.assertj.core.api.Assertions.assertThat(mailService.checkAuthMail(request)).isFalse();
    }

    @Test
    void 인증_코드_만료() {
        //given
        String testCode = "900900";
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        vop.set(testmail, testCode);

        //when
        vop.getAndDelete(testmail);

        MailVerifyRequest request = MailVerifyRequest.builder().email(testmail).code("900900").build();

        //then
        Assertions.assertThrows(NullPointerException.class, () -> mailService.checkAuthMail(request));
    }
}