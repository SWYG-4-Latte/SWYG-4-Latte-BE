package com.latte.member.controller;


import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import com.latte.member.service.AuthService;
import com.latte.member.service.MemberService;
import com.latte.response.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@CrossOrigin // CORS 허용 (인증이 필요한 것들)
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthService authService;


/**
 * Base64 : 디코딩 가능
 * Header -> HS256
 * PayLoad -> username
 * signature => Header + Payload + 코스
 */



    /**
     * 사용자 마이페이지
     * @return
     */
    @Secured("ROLE_USER")
    @GetMapping("/mypage/{seq}")
    public String myPage(@PathVariable String id) {

        authService.getMemberInfo(id);

        return "member/myPage";
    }

    /**
     * 관리자 페이지
     * @return
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin")
    public String admin() {


        return "member/admin";
    }
}
