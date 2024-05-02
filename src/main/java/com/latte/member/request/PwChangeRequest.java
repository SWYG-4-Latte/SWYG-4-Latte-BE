package com.latte.member.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class PwChangeRequest {

    // 사용자 ID
    private String userId;

    // 사용자 PW
    private String userPw;

}
