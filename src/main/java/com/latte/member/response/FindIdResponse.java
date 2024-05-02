package com.latte.member.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class FindIdResponse {

    // 상태
    private boolean state;

    // 사용자 ID
    private String findId;



    public FindIdResponse() {
        super();
    }

    public FindIdResponse(boolean state, String findId) {
        super();
        this.state = state;
        this.findId = findId;

    }
}
