package com.latte.member.controller;

import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.response.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

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

    /**
     * [API] 회원가입
     *
     * @return
     */
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> saveMember(@RequestBody MemberRequest request) {

        //System.out.println("가입정보" + request);

        //request.setRole("ROLE_USER");
/*        String rawPassword = request.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        request.setPassword(encPassword);*/
        int res = authService.save(request);

        MemberResponse result = authService.getMemberInfo(request.getMbrId());
        String data;
        if(res > 0) {
            data = String.valueOf(result);
        } else {
            data = "FAIL";
        }


        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), data);
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
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody @Validated final MemberRequest request) {
       // Map<String, Object> response = new HashMap<>();

/*        String rawPassword = request.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        request.setPassword(encPassword);*/


        int res = authService.update(request);
        MemberResponse result = authService.getMemberInfo(request.getMbrId());
        String data;
        System.out.println("=============result" + result);
       if(res > 0) {
           data = String.valueOf(result);
        } else {
           data = "FAIL";
        }

        ResponseData<?> responseData = new ResponseData<>(HttpStatus.OK.value(), data);
        return new ResponseEntity<>(responseData, OK);
    }


    /**
     * 회원 정보 삭제 (회원 탈퇴)
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public void deleteMember(@PathVariable String id) {

        authService.deleteMember(id);

        System.out.println("회원가입 탈퇴 성공");
    }
}
