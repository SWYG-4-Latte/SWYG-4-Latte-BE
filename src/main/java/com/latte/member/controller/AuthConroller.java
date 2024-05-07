package com.latte.member.controller;

import com.latte.member.config.jwt.JwtToken;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.member.service.EmailService;
import com.latte.response.ResponseData;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;


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
     * @param
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam("mbrId") String id, @RequestParam("password") String password, HttpServletResponse response) {
        //String id = request.getMbrId();
        //String password = request.getPassword();
        log.info("request id = {}, password = {}", id, password);
        // log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        Map<String, Object> dataMap = new HashMap<>();

        String message = "";

        try {
            JwtToken jwtToken = authService.signIn(id, password, response);
            dataMap.put("jwtToken", jwtToken);
            message =  "로그인에 성공했습니다";

        } catch (BadCredentialsException e) {
            int existIdYn = authService.countMemberByLoginId(id);
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
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken = extractTokenFromRequest(request);

        // JWT 토큰이 존재하는 경우, 토큰을 삭제하여 로그아웃 처리
        if (jwtToken != null) {
            // JWT 토큰 삭제 로직: 쿠키나 헤더에서 토큰을 제거
            deleteTokenFromClient(request, response);
            ResponseData<?> responseData = new ResponseData<>("로그아웃 되었습니다.", null);
            return new ResponseEntity<>(responseData, OK);
        } else {
            ResponseData<?> responseData = new ResponseData<>("로그아웃이 실패하였습니다.", null);
            return new ResponseEntity<>(responseData, OK);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // 클라이언트의 요청에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer " 다음의 토큰 부분만 추출
        }

        return null;
    }

    private void deleteTokenFromClient(HttpServletRequest request, HttpServletResponse response) {
        // 클라이언트의 쿠키나 헤더에서 JWT 토큰 삭제 로직 구현
        // 예시: 쿠키에서 삭제하는 경우
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    cookie.setMaxAge(0); // 쿠키 만료시킴으로써 삭제
                    response.addCookie(cookie);
                    break;
                }
            }
        }
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
    public ResponseEntity<?> update(@PathVariable("seq") int seq, @RequestBody @Validated final MemberRequest request) {


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



    // [API] 아이디 찾기
    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestParam("nickname") String nickname, @RequestParam("email") String email) throws Exception {

        FindIdResponse member = authService.findIdByNameEmail(nickname, email);

        MemberResponse user = authService.getMemberInfo(member.getMbrId());

        String message = "";
        String authInfo = null;
        if(member.getMbrId() == null || member.getMbrId() == "") {
            message = "해당 정보로 가입한 아이디가 없습니다.";
        } else if (member.getDeleteYn().equals("Y")) {
            message = "해당 아이디는 탈퇴한 회원입니다.";
        } else {
            authInfo = authService.saveTempAuthInfo(user.getMbrNo());
            message = "회원님의 아이디는 " + member.getMbrId() + "입니다.";
        }

        ResponseData<?> responseData = new ResponseData<>(message, authInfo);
        return new ResponseEntity<>(responseData, OK);
    }


    @PostMapping("/findPw")
    public ResponseEntity<?> forgotPassword(@RequestParam("mbrId") String id, @RequestParam("email") String email) throws Exception {

        MemberResponse member = authService.getMemberInfo(id);

        // 유효회원 여부 검사
        //int existUserId = authService.countMemberByLoginId(id);

        //int existUserEmail = authService.countMemberByEmail(email);

        int countByIdEmail = authService.countByIdEmail(id, email);

        String authInfo = null;

        String message = "";

        if(countByIdEmail == 0) {
            message = "아이디와 이메일을 다시 확인해주세요";
/*        } else if(existUserEmail == 0) {
            message = "해당 정보로 가입한 이메일이 없습니다.";*/
        } else {
            authInfo = authService.saveTempAuthInfo(member.getMbrNo());
            message = "인증번호를 전송하였습니다. 이메일을 확인해주세요";
        }

        ResponseData<?> responseData = new ResponseData<>(message, authInfo);
        return new ResponseEntity<>(responseData, OK);

    }


    @PostMapping("/update_pw")
    public ResponseEntity<?> updatePassword(@RequestParam("mbrNo") int mbrNo, @RequestParam("password") String password) throws Exception {


        boolean change = authService.updatePassword(mbrNo, password);

        String message = null;

        if(change) {
            message = "비밀번호 변경에 성공하였습니다.";
        } else {
            message = "비밀번호 변경에 실패하였습니다.";
        }

        ResponseData<?> responseData = new ResponseData<>(message, change);
        return new ResponseEntity<>(responseData, OK);

    }





}