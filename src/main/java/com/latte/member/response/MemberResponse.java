package com.latte.member.response;

import com.latte.member.request.MemberRequest;
import groovy.transform.builder.Builder;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;


@Getter
@Setter
@ToString
public class MemberResponse {


    private String mbrId;          // ID

    private String mbrName;         // 이름

    private String password;        // PW

    private String nickname;        // 닉네임

    private String cellPhone;       // 연락처

    private String email;           // 이메일

    private Gender gender;          // 성별

    private boolean pregnancy;      // 임신 여부

    private int pregMonth;          // 임신 개월 수

    private String allergy;         // 알러지

    private String symptom;         // 카페인 섭취 후 증상

    private String imgUrl;         // 이미지

    private String role;            // 권한(role_user, role_admin)

    private String age;             // 나이

    private String deleteYn;       // 회원존재 여부

    private String cupDay;          // 하루 카페인 잔

    private Timestamp regDate;      // 등록일

    private Timestamp updateDate;   // 변경일


    public void clearPassword() {
        this.password = "";
    }

    public List<MemberResponse> setMemberList(List<MemberResponse> memberList) {
        return memberList;
    }
}
