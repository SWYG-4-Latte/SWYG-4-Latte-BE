package com.latte.member.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

// 어떤 회원이 API를 요청했는지 조회(test용)
public class SecurityUtil {

    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information");
        }
        User user = (User) authentication.getPrincipal();
        return user.getUsername();
    }
}
