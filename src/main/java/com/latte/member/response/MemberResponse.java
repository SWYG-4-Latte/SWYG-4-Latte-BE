package com.latte.member.response;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;


@Getter
@Setter
@ToString
public class MemberResponse implements UserDetails {


    private int mbrNo;           // primary key

    private String mbrId;          // ID

    //private String mbrName;         // 이름

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

    private String code;

    private String codeDate;

    public void clearPassword() {
        this.password = "";
    }

    public List<MemberResponse> setMemberList(List<MemberResponse> memberList) {
        return memberList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getUsername() {
        return mbrId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
