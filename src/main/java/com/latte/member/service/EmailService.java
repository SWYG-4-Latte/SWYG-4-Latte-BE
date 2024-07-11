package com.latte.member.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.latte.member.exception.NotCodeException;
import com.latte.member.exception.VerifyCodeResult;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.response.*;
import com.latte.response.ResponseData;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("duswlskfk42@naver.com");
            helper.setTo(tempAuthResponse.getEmail());

            String htmlContent = null;


            // 아이디 찾기
            if (tempAuthResponse.getKind().equals("id")) {
                helper.setSubject("[라떼핏] 아이디 발송 안내");
                htmlContent = loadHtmlTemplate("templates/email_template.html");
                htmlContent = htmlContent.replace("{{authNumber}}", tempAuthResponse.getAuthNumber());
            } else {
                // 비밀번호 찾기
                helper.setSubject("[라떼핏] 인증번호 발송 안내");
                htmlContent = loadHtmlTemplate("templates/pw_template.html");
                htmlContent = htmlContent.replace("{{authNumber}}", tempAuthResponse.getAuthNumber());

                LocalDateTime codeDate = LocalDateTime.now();
                authMapper.insertCode(tempAuthResponse.getEmail(), tempAuthResponse.getAuthNumber(), codeDate);
            }

            helper.setText(htmlContent, true);

            mailSender.send(message);
            return tempAuthResponse.getAuthNumber();
        } catch (MailException | MessagingException e) {
            e.printStackTrace();
            throw new IllegalAccessException();
        }
       /* try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("duswlskfk42@naver.com");
            //message.setFrom("lattefit.team@gmail.com");
            message.setTo(tempAuthResponse.getEmail());

            // 아이디 찾기
            if(tempAuthResponse.getKind().equals("id")) {
                message.setSubject("[라떼핏] 아이디 발송 안내");
                //message.setText("라떼핏 아이디 찾기 안내");
                message.setText("라떼핏 아이디 찾기 안내 \n 아이디 : " + tempAuthResponse.getAuthNumber());
            } else {
                // 비밀번호 찾기
                message.setSubject("[라떼핏] 인증번호 발송 안내");
                //message.setText("라떼핏 비밀번호 찾기 안내");
                message.setText("라떼핏 비밀번호 찾기 안내 \n 인증번호 : " + tempAuthResponse.getAuthNumber());

                LocalDateTime codeDate = LocalDateTime.now();
                authMapper.insertCode(tempAuthResponse.getEmail(), tempAuthResponse.getAuthNumber(), codeDate);
            }

            mailSender.send(message);
            return tempAuthResponse.getAuthNumber();
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw new IllegalAccessException();
        }*/
    }


    private String loadHtmlTemplate(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
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
