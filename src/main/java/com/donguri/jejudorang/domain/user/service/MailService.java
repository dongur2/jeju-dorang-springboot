package com.donguri.jejudorang.domain.user.service;


public interface MailService {
    void sendAuthMail(String to, String subject, String text);
}
