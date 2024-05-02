package com.latte.member.controller;

import com.latte.member.config.SecurityUtil;
import com.latte.member.config.jwt.JwtToken;
import com.latte.member.request.FindIdRequest;
import com.latte.member.request.LoginRequest;
import com.latte.member.request.MemberRequest;
import com.latte.member.request.PwChangeRequest;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.response.PwChangeResponse;
import com.latte.member.response.SendOtpResponse;
import com.latte.member.service.AuthService;
import com.latte.member.service.EmailService;
import com.latte.response.ResponseData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthConroller {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username")
    private String from;

    private final JavaMailSender javaMailSender;



    /**
     * 로그인 페이지
     * @return
     */
/*
    @GetMapping("/login")
    public String loginForm() {
        return "member/loginForm";
    }

    */
/**
     * 회원가입 페이지
     * @return
     *//*

    @GetMapping("/signup")
    public String joinForm() {
        return "member/joinForm";
    }
*/


    /**
     * 로그인 API
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String id = request.getMbrId();
        String password = request.getPassword();
        log.info("request id = {}, password = {}", id, password);
       // log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());


        Map<String, Object> dataMap = new HashMap<>();

        String message = "";

        try {
            JwtToken jwtToken = authService.signIn(id, password, response);
            dataMap.put("jwtToken", jwtToken);
            message =  "로그인에 성공했습니다";

        } catch (BadCredentialsException e) {
            // 아이디 또는 비밀번호가 틀렸을 때
            dataMap.put("jwtToken", null);
            message =  "아이디 또는 비밀번호를 잘못 입력했습니다.";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);

    }


    /**
     * 로그아웃 API
     * @param
     * @return
     */
/*    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst();

        if (refreshTokenCookie.isPresent()) {
            refreshTokenCookie.get().setMaxAge(0);
            refreshTokenCookie.get().setPath("/"); // 쿠키 경로 설정
            response.addCookie(refreshTokenCookie.get());
        } // refreshTokenCookie 삭제. HttpOnly여서 서버에서 삭제

        String accessToken = request.getHeader(JwtTokenConstants.HEADER_AUTHORIZATION)
                .replace(JwtTokenConstants.TOKEN_PREFIX, "");

        Jwt jwt = tokenService.getAccessTokenInfo(accessToken);
        Long loggedoutId = kakaoLogout(jwt.getSocialAccessToken()); // 카카오 로그아웃

        if (loggedoutId == null) {
            throw new RuntimeException("logout failed");
        }
    }*/

    @PostMapping("/test")
    @ResponseBody
    public String test() {
        return SecurityUtil.getCurrentUsername();
    }

    /**
     * [API] 회원가입
     *
     * @return
     */
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> saveMember(@RequestBody MemberRequest request){

        boolean res = authService.save(request);

        MemberResponse result = authService.getMemberInfo(request.getMbrId());
        String message = "";

        Map<String, Object> dataMap = new HashMap<>();

        if(res) {
            dataMap.put("result", result); // MemberResponse를 Map에 추가
            message = "회원 가입에 성공했습니다.";
        } else {
            message = "회원 가입에 실패했습니다.";
        }


        dataMap.put("confirmId", res); // res 값을 Map에 추가


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }

    /**
     * [API] 회원수정
     * @param id
     *
     *
     * @param request
     * @return
     */
/*
    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Validated final MemberRequest request) {


        String message = "";

        MemberResponse bfData = authService.getMemberInfo(id);
        String seq = bfData.getMbrNo();
        request.setMbrNo(seq);
        boolean res = authService.update(request);

        Map<String, Object> dataMap = new HashMap<>();


        if(res) {
            MemberResponse result = authService.getMemberSeq(seq);
            dataMap.put("result", result); // MemberResponse를 Map에 추가
            message = "회원 수정에 성공했습니다.";
        } else {
            message = "회원 수정에 실패했습니다.";
        }


        dataMap.put("confirmId", res); // res 값을 Map에 추가


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

*/


    /**
     * 회원 정보 삭제 (회원 탈퇴)
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMember(@PathVariable String id) {

        boolean res = authService.deleteMember(id);
        String data = "";
        String message = "";
        if (res) {
            data = "true";
            message = "회원 탈퇴에 성공했습니다.";
        } else {
            data = "false";
            message = "회원 탈퇴에 실패했습니다.";
        }

        ResponseData<?> responseData = new ResponseData<>(message, data);
        return new ResponseEntity<>(responseData, OK);
    }



    //등록된 이메일로 임시비밀번호를 발송하고 발송된 임시비밀번호로 사용자의 pw를 변경하는 컨트롤러
/*    @PostMapping("/find-password")
    public @ResponseBody void findPassword(String userName, String userEmail) {
        MailDto dto = sendEmailService.createMailAndChangePassword(userEmail, userName);
        sendEmailService.mailSend(dto);

        MemberResponse member = authService.findByEmail(userEmail);
        if(member.getEmail() != null && !member.getEmail().equals("")) {
            String password = authService.getTempPassword();
            member.pa
        }


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo();

    }*/


    // [API] 아이디 찾기
    @PostMapping("/findId")
    public ResponseEntity<?> findId(FindIdRequest request) {

        FindIdResponse member = authService.findIdByNameEmail(request.getUserName(), request.getEmail());

        String message = "";
        String data = "";

        if(member.getFindId() == null || member.getFindId() == "") {
            message = "해당 정보로 가입한 아이디가 없습니다.";
        } else {
            message = "회원님의 아이디는 " + member.getFindId() + "입니다.";
            data = String.valueOf(member);
        }

        ResponseData<?> responseData = new ResponseData<>(message, data);
        return new ResponseEntity<>(responseData, OK);
    }

    // [API] 비밀번호 변경 > 본인인증번호 발송
/*    @PostMapping("/sendOtp")
    public SendOtpResponse sendOtp(String mbrId, String email ) {

        // 유효회원 여부 검사
        boolean existsUser = false;
            existsUser = tbMemberRepository.existsByUserIdAndUserEmail(request.getUserId(), request.getUserInfo());

        // 인증번호 발송
        if(!existsUser) {
            return new SendOtpResponse(false, "해당 정보로 가입한 아이디가 없습니다.");
        }
        else {
            String resOtp = "false";

            // 발송로직
            resOtp = mailSend.balsongMailSend(request.getUserInfo());


            // 발송된 인증번호 저장
            if(resOtp.equals("false")) {
                return new SendOtpResponse(false, "인증번호 발송에 실패했습니다. 다시 요청해주세요");
            }
            else {
                TbAuthLog ettAuthLog = tbAuthLogRepository.findByUserId(request.getUserId());

                if(ettAuthLog!=null) {
                    ettAuthLog.setAuthNum(resOtp);
                    ettAuthLog.setAuthType(request.getOptType().equals("E") ? "MAIL" : "PN");
                    ettAuthLog.setAuthLoc(request.getUserInfo());
                    ettAuthLog.setModId(request.getUserId());
                    ettAuthLog.setModDate(getToday());
                }
                else {
                    ettAuthLog = new TbAuthLog();
                    ettAuthLog.setUserId(request.getUserId());
                    ettAuthLog.setAuthNum(resOtp);
                    ettAuthLog.setAuthType(request.getOptType().equals("E") ? "MAIL" : "PN");
                    ettAuthLog.setAuthLoc(request.getUserInfo());
                    ettAuthLog.setRegId(request.getUserId());
                    ettAuthLog.setRegDate(getToday());
                    ettAuthLog.setModId(request.getUserId());
                    ettAuthLog.setModDate(getToday());
                }

                tbAuthLogRepository.save(ettAuthLog);
            }

        }

        return new SendOtpResponse(true, "");
    }

    // [API] 비밀번호 변경 > 본인인증번호 비교 확인
    @PostMapping("/checkOtp")
    public CheckOtpResponse checkOtp(CheckOtpRequest request) {

        return memberService.checkOtp(request);
    }

    // [API] 비밀번호 변경
    @PostMapping("/pwChange")
    public PwChangeResponse pwChange(HttpSession session, PwChangeRequest request) {
        request.setUserId(SessionUtil.getUserId(session));

        return memberService.pwChange(request);
    }*/



}
