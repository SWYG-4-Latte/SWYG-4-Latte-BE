package com.latte.member.config.auth;

import com.latte.member.response.MemberResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 생성된 Security session에 Authentication 타입 객체에서 UserDetails
public class PrincipalDetails implements UserDetails {

    private MemberResponse user;

    public PrincipalDetails(MemberResponse user) {
        this.user = user;
    }

    public MemberResponse getMember() {
        return user;
    }

    public int getMemberNo() {
        return user.getMbrNo();
    }

    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }
/*    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collection;
    }*/

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getMbrId();
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

    // 휴면계정 설정 시 사용
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 추가 메서드: SecurityContextHolder에 memberResponse를 저장하는 메서드
    public void setAuthenticationInContext() {
        // 현재 SecurityContext에서 Authentication을 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 만약 현재 인증된 사용자가 없다면, 새로운 UsernamePasswordAuthenticationToken을 생성하여 설정합니다.
        if (authentication == null) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(this, null, this.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
    
}
