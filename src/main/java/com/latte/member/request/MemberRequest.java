package com.latte.member.request;

import com.latte.member.response.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Collection;

@Getter
@Setter
@ToString
@Builder
public class MemberRequest implements UserDetails {


    private String mbrNo;          // 회원 번호

    //@NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String mbrId;          // ID

    //@NotBlank(message = "이름은 필수 입력 값입니다.")
    private String mbrName;         // 이름

    //@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    //@Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;        // PW

   // @NotEmpty(message = "닉네임을 입력해주세요")
    private String nickname;        // 닉네임

    //@NotEmpty(message = "핸드폰 번호를 입력해주세요")
    private String cellPhone;       // 연락처

    //@Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    //@NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;           // 이메일

    //@NotEmpty(message = "성별을 체크해주세요")
    private Gender gender;          // 성별

    //@NotEmpty(message = "임신여부를 체크해주세요")
    private boolean pregnancy;      // 임신 여부

    private int pregMonth;          // 임신 개월 수

    //@NotEmpty(message = "알러지여부를 체크해주세요")
    private String allergy;         // 알러지

    //@NotEmpty(message = "증상여부를 체크해주세요")
    private String symptom;         // 카페인 섭취 후 증상

    private String imageUrl;         // 이미지

    private String role;            // 권한(role_user, role_admin)

    //@NotEmpty(message = "나이를 입력해주세요")
    private String age;        // 나이

    private String cupDay;          // 하루 카페인 잔

    private String deleteYn;          // 회원탈퇴 여부

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        if (StringUtils.isEmpty(password)) {
            return;
        }
        password = passwordEncoder.encode(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    //private Boolean deleteYn;       // 회원탈퇴 여부

}
