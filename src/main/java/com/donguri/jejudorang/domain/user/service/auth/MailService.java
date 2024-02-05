package com.donguri.jejudorang.domain.user.service.auth;


import com.donguri.jejudorang.domain.user.dto.request.email.MailVerifyRequest;

public interface MailService {
    void sendAuthMail(String to, String subject, String text);

    boolean checkAuthMail(MailVerifyRequest mailVerifyRequest);
}
