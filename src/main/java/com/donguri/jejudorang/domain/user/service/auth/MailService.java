package com.donguri.jejudorang.domain.user.service.auth;


import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;
import jakarta.mail.MessagingException;

public interface MailService {

    // 아이디 찾기 메일 전송
    void sendMail(String to, String subject, String text) throws MessagingException;

    // 인증번호 메일 전송 & 인증번호 저장
    void sendAuthMail(String to, String subject, String text);

    // 인증번호 확인
    boolean checkAuthMail(MailVerifyRequest mailVerifyRequest);
}
