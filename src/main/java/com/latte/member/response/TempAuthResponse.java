package com.latte.member.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TempAuthResponse {

    private int mbrNo;

    private String email;

    private String authNumber;
}
