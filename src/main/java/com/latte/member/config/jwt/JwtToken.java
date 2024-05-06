package com.latte.member.config.jwt;

import com.latte.member.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {

    // JWT에 대한 인증 타입(Bearer)
    private String grantType;
    // 액세스 토큰
    private String accessToken;
    // 액세스 토큰이 만료가 되면 재발급 해주는 토큰
    private String refreshToken;

    //private MemberResponse memberResponse;

}
