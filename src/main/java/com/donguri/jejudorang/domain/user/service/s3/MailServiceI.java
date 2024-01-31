package com.donguri.jejudorang.domain.user.service.s3;

import com.donguri.jejudorang.domain.user.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class MailServiceI implements MailService {

    @Autowired private final JavaMailSender mailSender;

    public MailServiceI(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void sendAuthMail(String to, String subject, String text) {
        SimpleMailMessage message = createMailMessage(to, subject, text);

        try {
            mailSender.send(message);

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
