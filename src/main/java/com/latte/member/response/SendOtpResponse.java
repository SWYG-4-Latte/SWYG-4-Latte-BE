package com.latte.member.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class SendOtpResponse {


    // 상태
    private boolean state;

    // 에러 메세지
    private String msg;


    public SendOtpResponse() {
        super();
    }

    public SendOtpResponse(boolean state, String msg) {
        super();
        this.state = state;
        this.msg = msg;
    }
}
