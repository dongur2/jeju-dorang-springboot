package com.donguri.jejudorang.domain.user.api;

import com.donguri.jejudorang.domain.user.dto.MailVerifyRequest;
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
class UserControllerTest {
    @Value("${mail.test.email}") String testmail;

    @Autowired EntityManager em;
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
}