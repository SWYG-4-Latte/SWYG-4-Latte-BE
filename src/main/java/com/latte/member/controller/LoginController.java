package com.latte.member.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class LoginController {


    /**
     * 로그인
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "home";
    }

    /**
     * 회원가입
     * @return
     */
    @GetMapping("/join")
    public String join() {
        return "home";
    }

    /**
     * 회원 정보
     * @return
     */
    @GetMapping("/joinProc")
    public @ResponseBody String joinProc() {
        return "회원가입 완료!";
    }

    /**
     * 사용자
     * @return
     */
    @GetMapping("/user")
    public String user() {
        return "user";
    }

    /**
     * 관리자
     * @return
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
