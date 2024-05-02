package com.latte.latte.controller;

import com.latte.latte.DatabaseCleanUp;
import com.latte.member.config.jwt.JwtToken;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.Gender;
import com.latte.member.service.AuthService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;

import javax.print.attribute.standard.Media;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class MemberControllerTest {



    @Autowired
    DatabaseCleanUp databaseCleanUp;
    @Autowired
    AuthService memberService;
    @Autowired
    TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    private MemberRequest memberRequest;

    @PostMapping("/test")
    public String test() {
        return "<h1>test 통과</h1>";
    }

/*
    @BeforeEach
    void beforeEach() {
        // Member 회원가입
        memberRequest = MemberRequest.builder()
                .mbrId("latteTest")
                .mbrName("라뗴2")
                .password("12342")
                .nickname("latte2")
                .cellPhone("010-6396-2752")
                .email("kimyeonji2@naver.com")
                .gender(Gender.valueOf("F"))
                .pregnancy(false)
                .allergy("없어요2")
                .symptom("없어요2")
                .role("ROLE_USER")
                .age("23")
                .cupDay("3")
                .build();
    }

    @AfterEach
    void afterEach() {
        databaseCleanUp.truncateAllEntity();
    }

    @Test
    public void signUpTest() {

        // API 요청 설정
        String url = "http://localhost:" + randomServerPort + "/member/signup";
        ResponseEntity<MemberRequest> responseEntity = testRestTemplate.postForEntity(url, memberRequest, MemberRequest.class);

        // 응답 검증
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        MemberRequest savedMemberDto = responseEntity.getBody();
        assertThat(savedMemberDto.getMbrName()).isEqualTo(memberRequest.getMbrName());
        assertThat(savedMemberDto.getNickname()).isEqualTo(memberRequest.getNickname());

    }

    @Test
    public void signInTest() {
        memberService.save(memberRequest);

        MemberRequest memberRequest = MemberRequest.builder()
                .mbrId("latteTest")
                .password("1234").build();

        // 로그인 요청
        JwtToken jwtToken = memberService.signIn(memberRequest.getMbrId(), memberRequest.getPassword());

        // HttpHeaders 객체 생성 및 토큰 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwtToken.getAccessToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        log.info("httpHeaders = {}", httpHeaders);

        // API 요청 설정
        String url = "http://localhost:" + randomServerPort + "/auth/test";
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(url, new HttpEntity<>(httpHeaders), String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(memberRequest.getMbrName());
    }

*/

}
