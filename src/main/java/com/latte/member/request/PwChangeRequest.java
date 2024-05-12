package com.latte.member.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class PwChangeRequest {

    // 사용자 번호
    private int mbrNo;

    // 사용자 PW
    private String password;

}
