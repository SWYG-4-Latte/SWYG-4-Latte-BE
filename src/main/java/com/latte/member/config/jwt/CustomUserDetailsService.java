package com.latte.member.config.jwt;

import com.latte.member.mapper.MemberMapper;
import com.latte.member.response.MemberResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {


    private final MemberMapper memberRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(MemberMapper memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
/*        return memberRepository.findByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));*/

        Optional<MemberResponse> memberResponseOptional = Optional.ofNullable(memberRepository.findById(username));
        MemberResponse memberResponse = memberResponseOptional.orElseThrow(() ->
                new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
        return createUserDetails(memberResponse);

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
