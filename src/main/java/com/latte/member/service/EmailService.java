package com.latte.member.service;

import java.io.IOException;
import java.lang.reflect.Member;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.latte.member.exception.NotCodeException;
import com.latte.member.exception.VerifyCodeResult;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.*;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            //message.setFrom("lattefit.team@gmail.com");
            message.setTo(tempAuthResponse.getEmail());

            // 아이디 찾기
            if(tempAuthResponse.getKind().equals("id")) {
                message.setSubject("아이디 발송 안내");
                message.setText("아이디 : " + tempAuthResponse.getAuthNumber());
            } else {
                // 비밀번호 찾기
                message.setSubject("인증번호 발송 안내");
                message.setText("인증번호 : " + tempAuthResponse.getAuthNumber());

                LocalDateTime codeDate = LocalDateTime.now();
                authMapper.insertCode(tempAuthResponse.getEmail(), tempAuthResponse.getAuthNumber(), codeDate);
            }

            mailSender.send(message);
            return tempAuthResponse.getAuthNumber();
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw new IllegalAccessException();
        }
    }


    public VerifyCodeResult verifyCode(String email, String code) {

        int count = authMapper.verifyCode(email, code);


        FindIdResponse find = authMapper.findIdByNameEmail(email);

        ResponseData<?> responseData;
        // 10분 유효기간 체크
        if (count > 0) {
            System.out.println("=--------vLocalDateTime.now()" + find.getCodeDate());
            System.out.println("=--------vLocalDateTime.now()" + LocalDateTime.now().minusMinutes(10));
            if (find.getCodeDate().isAfter(LocalDateTime.now().minusMinutes(10))) {

                return new VerifyCodeResult("인증이 완료되었습니다.", true);
            } else {

                return new VerifyCodeResult( "입력 시간이 초과되었습니다. 다시 인증해주세요.", false);
            }
        } else {
            return new VerifyCodeResult( "인증번호가 일치하지 않습니다." ,false);
        }


    }


    public boolean deleteVerificationCode(String email) {

        try {
            return authMapper.deleteCode(email);
        }catch (Exception e) {
            throw new NotCodeException("인증코드 초기화가 실패하였습니다.");
        }

    }


}
