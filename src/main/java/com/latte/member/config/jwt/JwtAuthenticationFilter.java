package com.latte.member.config.jwt;

import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MemberResponse member = new MemberResponse("1", "testUser", "이름", "비밀번호", "닉네임", "연락처", "이메일", Gender.M,
                false, 0, "없어요", "", "이미지", "USER", "26", "N", "3", null, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
