package com.latte.member.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class FindIdRequest {

    // 사용자 이름
    private String userName;

    // 사용자 전화번호 또는 사용자 이메일 정보
    private String email;

}
