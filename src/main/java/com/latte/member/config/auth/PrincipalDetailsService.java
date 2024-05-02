package com.latte.member.config.auth;

import com.latte.member.mapper.AuthMapper;
import com.latte.member.mapper.MemberMapper;
import com.latte.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService  {

    @Autowired
    private AuthMapper authMapper;

    private final PasswordEncoder passwordEncoder;

    // 시큐리티 session에 authentication 정보가 들어감

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("유효하지 않은 사용자 이름입니다.");
        }

        MemberResponse memberEntity = authMapper.findById(username);
        if (memberEntity != null) {
            return new PrincipalDetails(memberEntity);
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(MemberResponse member) {
        return User.builder()
                .username(member.getMbrId())
                .password(passwordEncoder.encode(member.getPassword()))
                .roles(member.getRole())
                .build();
    }

}
