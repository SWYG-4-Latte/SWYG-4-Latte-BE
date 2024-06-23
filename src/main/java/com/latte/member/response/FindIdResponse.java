package com.latte.member.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
public class FindIdResponse {

    // 상태
    private boolean state;

    private String mbrNo;

    // 사용자 ID
    private String mbrId;

    private LocalDateTime codeDate;

    private String deleteYn;



    public FindIdResponse() {
        super();
    }

    public FindIdResponse(boolean state, String mbrNo, String mbrId, String deleteYn) {
        super();
        this.state = state;
        this.mbrNo = mbrNo;
        this.mbrId = mbrId;
        this.deleteYn = deleteYn;

    }
}
