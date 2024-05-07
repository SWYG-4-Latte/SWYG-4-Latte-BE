package com.latte.latte.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendEmailTest {

    final String HOST = "smtp.gmail.com";
    final int PORT = 587;
    final String FROM = "duswlskfk42@gmail.ocm";
    final String PASSWORD = "cdft zawq cntp sxoh";
    String emailTo = "duswlskfk42@naver.com";

    boolean auth = true;
    boolean starttls = true;
    boolean sslTrust = true;
    boolean ssl = false;

    @Test
    public void sendMAil() throws UnsupportedEncodingException, javax.mail.MessagingException, MessagingException {

        JavaMailSender sender = javaMailSender();
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        InternetAddress to = new InternetAddress();
        to.setAddress(emailTo);
        to.setPersonal(emailTo, "UTF-8");

        helper.setFrom(FROM);
        helper.setTo(String.valueOf(to));
        helper.setSubject("email title");
        helper.setText("email text");

        sender.send(message);
    }

    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);
        mailSender.setUsername(FROM);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

        props.setProperty("mail.smtp.auth", String.valueOf(auth));
        if (starttls)
            props.setProperty("mail.smtp.starttls.enable", "true");
        if (sslTrust)
            props.setProperty("mail.smtp.ssl.trust", "*");
        if (ssl)
            props.setProperty("mail.smtp.ssl.enable", "true");
        return mailSender;
    }

}