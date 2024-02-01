package com.donguri.jejudorang.domain.user.service.auth;


public interface MailService {
    void sendAuthMail(String to, String subject, String text);
}
