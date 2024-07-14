package com.latte.member.controller;

import com.latte.member.response.LoginResponse;
import com.latte.member.service.KakaoService;
import com.latte.response.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/api/users")
public class KakaoController {


    private final KakaoService kakaoService;
    // https://kauth.kakao.com/oauth/authorize?client_id=5c165585248b1b09c20c411387178149&redirect_uri=https://lattefit.swygbro.com/auth/login/kakao&response_type=code
    // https://lattefit.swygbro.com/auth/login/kakao
    // https://localhost:3000/auth/login/kakao
    //
    //web 버전
    @ResponseBody
    @GetMapping("/auth/login/oauth")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code,@RequestParam String redirectUri, HttpServletRequest request){

        String message = null;
        Map<String, Object> dataMap = new HashMap<>();

        try{
            // 현재 도메인 확인
            //String currentDomain = request.getServerName();
            String token = kakaoService.kakaoLogin(code, redirectUri).getAccessToken();
            message =  "로그인에 성공했습니다";
            dataMap.put("jwtToken", token);
        } catch (NoSuchElementException e) {
            message =  "로그인에 실패했습니다";
            dataMap.put("jwtToken", null);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Item Not Found");
        }

        ResponseData<?> responseData = new ResponseData<>(message, dataMap);
        return new ResponseEntity<>(responseData, OK);
    }


}
