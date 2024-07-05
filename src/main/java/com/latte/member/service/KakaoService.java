package com.latte.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latte.member.config.jwt.JwtToken;
import com.latte.member.config.jwt.JwtTokenProvider;
import com.latte.member.controller.AuthConroller;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.LoginResponse;
import com.latte.member.response.MemberResponse;
import com.latte.response.ResponseData;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoService {


    private final AuthMapper authMapper;
    //private final AuthTokensGenerator authTokensGenerator;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("5c165585248b1b09c20c411387178149")
    private String clientId;        // REST API 키

/*    @Value("https://localhost:8080/auth/login/oauth")
    private String redirectUri;*/




    @Autowired
    private AuthService authService;





    /** Web 버전 카카오 로그인 **/
    public LoginResponse kakaoLogin(String code, String redirectUri) {
        //0. 동적으로 redirect URI 선택
        //String redirectUri = selectRedirectUri(currentDomain);

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code, redirectUri);

        // 2. 토큰으로 카카오 API 호출
        HashMap<String, Object> userInfo= getKakaoUserInfo(accessToken);

        //3. 카카오ID로 회원가입 & 로그인 처리
        LoginResponse kakaoUserResponse= kakaoUserLogin(userInfo);

        return kakaoUserResponse;
    }

    //1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code, String redirectUri) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonNode.get("access_token").asText(); //토큰 전송
    }


    //2. 토큰으로 카카오 API 호출
    public HashMap<String, Object> getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo= new HashMap<String,Object>();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        //String nickname = jsonNode.get("properties").get("nickname").asText();

        userInfo.put("id",id);
        userInfo.put("email",email);
        //userInfo.put("nickname",nickname);

        return userInfo;
    }


    //3. 카카오ID로 회원가입 & 로그인 처리
    private LoginResponse kakaoUserLogin(HashMap<String, Object> userInfo){

        Map<String, Object> dataMap = new HashMap<>();
        ResponseData<?> responseData;
        String message;
        JwtToken jwtToken = null;
        Long uid = null;
        String kakaoEmail = null;

        try {
            uid= Long.valueOf(userInfo.get("id").toString());
            kakaoEmail = userInfo.get("email").toString();
            //String nickName = userInfo.get("nickname").toString();

            FindIdResponse kakaoUser = authMapper.findIdByNameEmail(kakaoEmail);

            MemberRequest kakaoMember = new MemberRequest();

            boolean res = false;
            if (kakaoUser == null) {    //회원가입
                kakaoMember.setMbrId(kakaoEmail);
                kakaoMember.setEmail(kakaoEmail);
                //kakaoMember.setNickname(nickName);
                kakaoMember.setLoginType("kakao");
                res = authService.save(kakaoMember);

               // MemberResponse member = authMapper.findById(kakaoUser.getMbrNo());




                if (res) {
                    //MemberResponse result = authService.getMemberInfo(member.getMbrId());
                    //dataMap.put("result", result); // MemberResponse를 Map에 추가
                    message = "회원 가입에 성공했습니다.";
                    log.info("회원가입 성공 request id = {}", kakaoEmail);



                    // 회원가입 성공 시 로그인 수행
                    // 토큰 생성
                    jwtToken = jwtTokenProvider.kakaoGenerate(uid.toString(), kakaoEmail);
                    dataMap.put("jwtToken", jwtToken);
                    log.info("자동 로그인 성공 request id = {}, token = {}", kakaoEmail, jwtToken.getAccessToken());
                } else {
                    message = "회원 가입에 실패했습니다.";
                    log.error("회원가입 실패 request id = {}", kakaoEmail);

                }
            } else {
                jwtToken = jwtTokenProvider.kakaoGenerate(uid.toString(), kakaoEmail);
                dataMap.put("jwtToken", jwtToken);
                log.info("자동 로그인 성공 request id = {}, token = {}", kakaoEmail, jwtToken.getAccessToken());
            }


/*

            dataMap.put("confirmId", existIdYn); // res 값을 Map에 추가
            dataMap.put("confirmNickname", existNicknameYn);
            dataMap.put("confirmEmail", existIdEmailYn);*/

        } catch (Exception e) {
            message = "회원 가입 중 오류가 발생했습니다.";
            log.error("회원가입 중 예외 발생: {}", e.getMessage(), e);
/*            return new ResponseEntity<>(new ResponseData<>(message, dataMap), HttpStatus.INTERNAL_SERVER_ERROR);*/
        }


        assert jwtToken != null;
        return new LoginResponse(uid ,"", kakaoEmail, jwtToken.getAccessToken());
    }


}
