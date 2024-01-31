package com.donguri.jejudorang.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.timeout}")
    private int timeOut;

    @Value("${spring.mail.properties.mail.smtp.writetimeout}")
    private int writeTimeOut;

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private int connectionTimeOut;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean tls;

    @Value("${spring.mail.properties.mail.smtp.debug.enable}")
    private boolean debug;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(getMailProperties());

        return mailSender;
    }


    private Properties getMailProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", tls);
        props.put("mail.debug", debug);
        props.put("mail.smtp.connectiontimeout", connectionTimeOut);
        props.put("mail.smtp.timeout", timeOut);
        props.put("mail.smtp.writetimeout", writeTimeOut);

        return props;
    }
}
