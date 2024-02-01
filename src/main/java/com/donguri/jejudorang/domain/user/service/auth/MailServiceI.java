package com.donguri.jejudorang.domain.user.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class MailServiceI implements MailService {

    @Autowired private JavaMailSender mailSender;
    @Autowired private RedisTemplate<String, String> redisTemplate;



    @Override
    @Transactional
    public void sendAuthMail(String to, String subject, String text) {
        SimpleMailMessage message = createMailMessage(to, subject, text);

        try {
            mailSender.send(message);

            /*
            * {이메일 : 인증 번호} Redis에 저장
            * */
            ValueOperations<String, String> vop = redisTemplate.opsForValue();
            if(vop.get(to) != null) {
                vop.set(to, text);
            } else {
                // 인증 되지 않은 이메일 키가 존재할 경우 삭제 후 새로운 코드 저장
                vop.getAndDelete(to);
                vop.set(to, text);
            }
            log.info("Redis에 인증 번호 저장 완료 key {} : value {}", to, vop.get(to));

        } catch (Exception e) {
            log.error("이메일 전송에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

    private SimpleMailMessage createMailMessage(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);

        return msg;
    }
}
