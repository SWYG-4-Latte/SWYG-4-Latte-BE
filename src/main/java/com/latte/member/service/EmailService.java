package com.latte.member.service;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import com.latte.member.mapper.AuthMapper;
import com.latte.member.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    //@Autowired
    //private JavaMailSender mailSender;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private AuthMapper authMapper;


    //@Async("threadPoolTaskExecutor")
    public void sendPasswordForgotMessage(TempAuthResponse tempAuthResponse) {
        log.info("send Email");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kimyeonji715@gmail.com");
        message.setTo(tempAuthResponse.getEmail());
        message.setSubject("라떼 임시 비밀번호 발송 안내");
        message.setText("임시비밀번호 : " + tempAuthResponse.getPassword());
        mailSender.send(message);
    }


    // SSL 연결 사용하지 않도록 설정
    public void configureMailSender() {
        javaMailSender.getJavaMailProperties().setProperty("mail.smtp.ssl.enable", "false");
    }

}
