package com.latte.member.service;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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
import org.springframework.mail.MailException;
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
    private AuthMapper authMapper;


    //@Async("threadPoolTaskExecutor")
    public boolean sendEmail(TempAuthResponse tempAuthResponse) throws Exception {
        log.info("send Email");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("illywilly2750@gmail.com");
            message.setTo(tempAuthResponse.getEmail());
            message.setSubject("임시 비밀번호 발송 안내");
            message.setText("임시비밀번호 : " + tempAuthResponse.getPassword());

            mailSender.send(message);
            return true;
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw new IllegalAccessException();
        }
    }


}
