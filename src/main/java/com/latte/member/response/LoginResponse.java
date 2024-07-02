package com.latte.member.response;


import com.latte.member.config.jwt.JwtToken;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {


    private Long id;
    private String nickname;
    private String email;
    private String accessToken;


    public LoginResponse(Long id, String nickname, String email, String accessToken) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.accessToken = accessToken;
    }

}
