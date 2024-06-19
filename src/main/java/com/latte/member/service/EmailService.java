package com.latte.member.service;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import org.springframework.data.redis.core.RedisTemplate;
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

    //private final RedisTemplate<String, String> redisTemplate;


    //@Async("threadPoolTaskExecutor")
    public String sendEmail(TempAuthResponse tempAuthResponse) throws Exception {
        log.info("send Email");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("duswlskfk42@naver.com");
            message.setTo(tempAuthResponse.getEmail());

            // 아이디 찾기
            if(tempAuthResponse.getKind().equals("id")) {
                message.setSubject("아이디 발송 안내");
                message.setText("이이디 : " + tempAuthResponse.getAuthNumber());
            } else {
                // 비밀번호 찾기
                message.setSubject("인증번호 발송 안내");
                message.setText("인증번호 : " + tempAuthResponse.getAuthNumber());
                authMapper.insertCode(tempAuthResponse.getEmail(), tempAuthResponse.getAuthNumber());
            }

            mailSender.send(message);
            return tempAuthResponse.getAuthNumber();
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw new IllegalAccessException();
        }
    }


    public boolean verifyCode(String email, String code) {

        int count = authMapper.verifyCode(email, code);

        if(count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteVerificationCode(String email) {

        return authMapper.deleteCode(email);
    }


}
