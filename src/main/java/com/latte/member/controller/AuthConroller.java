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
import org.apache.ibatis.annotations.Param;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import static org.springframework.http.HttpStatus.OK;

// 로그인, 로그아웃
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

    @Autowired
    private final JavaMailSender javaMailSender;


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
            int existIdYn = authService.countMemberByLoginId(request.getMbrId());
            dataMap.put("jwtToken", null);
            // 아이디가 존재하지 않다면
            if(existIdYn == 0) {
                // 아이디가 틀렸을 때
                message =  "아이디를 잘못 입력했습니다.";
            } else {
                // 비밀번호가 틀렸을 때
                message =  "비밀번호를 잘못 입력했습니다.";
            }
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
    public ResponseEntity<?> saveMember(@RequestBody MemberRequest request) {

        String existIdYn = null;
        String existNicknameYn = null;
        String existIdEmailYn = null;

        int countMemberById = authService.countMemberByLoginId(request.getMbrId());
        int countMemberByName = authService.countMemberByNickname(request.getNickname());
        int countMemberByEmail = authService.countMemberByEmail(request.getEmail());
        String message = "";
        Map<String, Object> dataMap = new HashMap<>();

        if (countMemberById > 0) {
            existIdYn = "false";
            message = "아이디가 이미 존재합니다.";
        } else if (countMemberByName > 0) {
            existNicknameYn = "false";
            message = "닉네임이 이미 존재합니다.";
        } else if (countMemberByEmail > 0) {
            existIdEmailYn = "false";
            message = "이메일이 이미 존재합니다.";
        } else {
            existIdYn = "true";
            existNicknameYn = "true";
            existIdEmailYn = "true";
            boolean res = authService.save(request);
            if (res) {
                MemberResponse result = authService.getMemberInfo(request.getMbrId());
                dataMap.put("result", result); // MemberResponse를 Map에 추가
                message = "회원 가입에 성공했습니다.";
            } else {
                message = "회원 가입에 실패했습니다.";
            }
        }

        dataMap.put("confirmId", existIdYn); // res 값을 Map에 추가
        dataMap.put("confirmNickname", existNicknameYn);
        dataMap.put("confirmEmail", existIdEmailYn);




        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }

    /**
     * [API] 회원수정
     * @param seq
     * @param request
     * @return
     */
    @PostMapping("/update/{seq}")
    public ResponseEntity<?> update(@PathVariable int seq, @RequestBody @Validated final MemberRequest request) {


        String existIdYn = null;
        String existNicknameYn = null;
        String existIdEmailYn = null;

        int countMemberById = authService.countMemberByLoginId(request.getMbrId());
        int countMemberByName = authService.countMemberByNickname(request.getNickname());
        int countMemberByEmail = authService.countMemberByEmail(request.getEmail());
        String message = "";
        Map<String, Object> dataMap = new HashMap<>();


        if (countMemberById > 0) {
            existIdYn = "false";
            message = "아이디가 이미 존재합니다.";
        } else if (countMemberByName > 0) {
            existNicknameYn = "false";
            message = "닉네임이 이미 존재합니다.";
        } else if (countMemberByEmail > 0) {
            existIdEmailYn = "false";
            message = "이메일이 이미 존재합니다.";
        } else {
            existIdYn = "true";
            existNicknameYn = "true";
            existIdEmailYn = "true";
            request.setMbrNo(seq);
            boolean res = authService.update(request);
            if (res) {
                MemberResponse result = authService.getMemberSeq(seq);
                dataMap.put("result", result); // MemberResponse를 Map에 추가
                message = "회원 수정에 성공했습니다.";
            } else {
                message = "회원 수정에 실패했습니다.";
            }
        }

        dataMap.put("confirmId", existIdYn); // res 값을 Map에 추가
        dataMap.put("confirmNickname", existNicknameYn);
        dataMap.put("confirmEmail", existIdEmailYn);


        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }



    /**
     * 회원 정보 삭제 (회원 탈퇴)
     * @param
     * @return
     */
    @DeleteMapping("/delete/{seq}")
    @ResponseBody
    public ResponseEntity<?> deleteMember(@PathVariable("seq") int seq) {

        boolean res = authService.deleteMember(seq);
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


    @PostMapping("/findPw")
    public ResponseEntity<?> forgotPassword(@RequestParam("mbrId") String id, @RequestParam("email") String email) throws Exception {

        MemberResponse member = authService.getMemberInfo(id);

        // 유효회원 여부 검사
        int existUserId = authService.countMemberByLoginId(id);

        int existUserEmail = authService.countMemberByEmail(email);

        int countByIdEmail = authService.countByIdEmail(id, email);

        boolean authInfo = false;

        String message = "";

        if(countByIdEmail == 0) {
            message = "아이디와 이메일을 다시 확인해주세요";
/*        } else if(existUserEmail == 0) {
            message = "해당 정보로 가입한 이메일이 없습니다.";*/
        } else {
                authInfo = authService.saveTempAuthInfo(member.getMbrNo());
        }

        ResponseData<?> responseData = new ResponseData<>(message, authInfo);
        return new ResponseEntity<>(responseData, OK);

    }



/*
    @GetMapping("/")
    public String index() throws MessagingException, UnsupportedEncodingException {

        String to = "hhsung0120@naver.com";
        String from = "hshan@test.com";
        String subject = "test";

        StringBuilder body = new StringBuilder();
        body.append("<html> <body><h1>Hello </h1>");
        body.append("<div>테스트 입니다2. <img src=\"cid:flower.jpg\"> </div> </body></html>");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

        mimeMessageHelper.setFrom(from,"hshan");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body.toString(), true);

        FileSystemResource fileSystemResource = new FileSystemResource(new File("C:/Users/HOME/Desktop/test.txt"));
        mimeMessageHelper.addAttachment("또르르.txt", fileSystemResource);

        FileSystemResource file = new FileSystemResource(new File("C:/Users/HOME/Desktop/flower.jpg"));
        mimeMessageHelper.addInline("flower.jpg", file);

        javaMailSender.send(message);

        return "하이";
    }
}
*/



    // [API] 비밀번호 변경 > 본인인증번호 발송
   /* @PostMapping("/sendOtp")
    public SendOtpResponse sendOtp(String mbrId, String email ) {

        // 유효회원 여부 검사
        boolean existsUser = false;
            existsUser = authService.findIdByNameEmail(mbrId, email);

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
    }*/




/*
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
    }
*/



}