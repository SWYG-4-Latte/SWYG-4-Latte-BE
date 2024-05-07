package com.latte.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@ComponentScan
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587); // Naver SMTP 포트 번호
        javaMailSender.setUsername("illywilly2750@gmail.com");
        javaMailSender.setPassword("falcon2453!");


        //javaMailSender.setPort(465);
        getMailProperties();

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.imap.auth.login.disable", "true");
        properties.setProperty("mail.imap.auth.plain.disable", "true");
        //properties.setProperty("mail.smtp.ssl.trust","smtp.naver.com");
        //properties.setProperty("mail.smtp.ssl.enable","false");
        return properties;
    }
}