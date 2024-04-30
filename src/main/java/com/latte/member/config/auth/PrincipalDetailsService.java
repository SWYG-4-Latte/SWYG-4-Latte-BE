package com.latte.member.config.auth;

import com.latte.member.mapper.AuthMapper;
import com.latte.member.mapper.MemberMapper;
import com.latte.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    //private AuthMapper authMapper;

    private AuthMapper authMapper;


    // 시큐리티 session에 authentication 정보가 들어감
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        MemberResponse memberEntity = authMapper.findById(id);
        if(memberEntity != null) {
            return new PrincipalDetails(memberEntity);
        }
        return null;
    }
}
