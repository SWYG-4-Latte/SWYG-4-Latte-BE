package com.latte.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.member.config.jwt.JwtToken;
import com.latte.member.request.LoginRequest;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthConroller {

    @Autowired
    private AuthService authService;


    /**
     * 로그인 페이지
     * @return
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/loginForm";
    }

    /**
     * 회원가입 페이지
     * @return
     */
    @GetMapping("/signup")
    public String joinForm() {
        return "member/joinForm";
    }


    @PostMapping("/login")
    @ResponseBody
    public JwtToken login(@RequestBody LoginRequest request) {
        String id = request.getMbrId();
        String password = request.getPassword();
        JwtToken jwtToken = authService.signIn(id, password);
        log.info("request id = {}, password = {}", id, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @PostMapping("/test")
    public String test() {
        return "success";
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
}
