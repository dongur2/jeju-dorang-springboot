package com.donguri.jejudorang.domain.user.service.auth;

import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class MailServiceI implements MailService {

    private int codeExpired;
    @Autowired private final JavaMailSender mailSender;
    @Autowired private final RedisTemplate<String, String> redisTemplate;

    public MailServiceI(@Value("${redis.email-code.expired}") int codeExpired, JavaMailSender mailSender, RedisTemplate<String, String> redisTemplate) {
        this.codeExpired = codeExpired;
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }


    /*
    * 아이디 메일 전송
    * */
    @Override
    @Transactional
    public void sendMail(String to, String subject, String text) throws MessagingException {

        try {
            MimeMessage message = createHtmlMessage(to, subject, text);
            mailSender.send(message);

        } catch (Exception e) {
            log.error("이메일 전송에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }

    private MimeMessage createHtmlMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true); // html body

        return message;
    }


    /*
    * 인증 번호 메일 전송
    * */
    @Override
    @Transactional
    public void sendAuthMail(String to, String subject, String text) {

        try {
            SimpleMailMessage message = createMailMessage(to, subject, text);
            mailSender.send(message);

            /*
            * {이메일 : 인증 번호} Redis에 저장
            * */
            ValueOperations<String, String> vop = redisTemplate.opsForValue();
            if(vop.get(to) != null) {
                vop.set(to, text, codeExpired, TimeUnit.MILLISECONDS);

            } else {
                // 인증 되지 않은 이메일 키가 존재할 경우 삭제 후 새로운 코드 저장
                vop.getAndDelete(to);
                vop.set(to, text, codeExpired, TimeUnit.MILLISECONDS);

            }
            log.info("Redis에 인증 번호 저장 완료 key {} : value {}", to, vop.get(to));

        } catch (Exception e) {
            log.error("이메일 전송에 실패했습니다 : {}", e.getMessage());
            throw e;
        }
    }


    /*
    * 인증번호 확인
    * */
    @Override
    @Transactional
    public boolean checkAuthMail(MailVerifyRequest mailVerifyRequest) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        try {
            String originalCode = vop.get(mailVerifyRequest.email());
            return originalCode.equals(mailVerifyRequest.code());

        } catch (NullPointerException e) {
            log.error("인증 번호가 만료되었습니다.");
            throw new NullPointerException("인증 번호가 만료되었습니다.");

        } catch (Exception e) {
            log.error("이메일 인증 실패 : {}", e.getMessage());
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
