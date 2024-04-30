package com.latte.member.service;

import com.latte.member.config.jwt.JwtToken;
import com.latte.member.config.jwt.JwtTokenProvider;
import com.latte.member.mapper.AuthMapper;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.MemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {



    @Autowired
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthMapper authMapper, PasswordEncoder passwordEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, JwtTokenProvider jwtTokenProvider) {
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 로그인 요청시 Authentication 객체 생성
     * @param mbrId
     * @param password
     * @return
     */
    public JwtToken signIn(String mbrId, String password) {
        // 1. mbrId + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(mbrId, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

    /**
     * 회원정보 등록
     * @param request
     */
    @Transactional
    public boolean save(MemberRequest request) {

        int existId = authMapper.countByLoginId(request.getMbrId());

        if(existId == 1) {
            System.out.println("========아이디가 존재합니다 ==========");
            return false;
        } else {
            // 권한 부여
            request.setRole("USER");
            // 회원탈퇴 여부
            request.setDeleteYn("N");

            request.encodingPassword(passwordEncoder);

            authMapper.insertMember(request);

            return true;
        }

    }

    /**
     * 회원정보 수정
     * @param
     */
    @Transactional
    public boolean update(MemberRequest request) {

        int existId = authMapper.countByLoginId(request.getMbrId());

        if(existId == 1) {
            System.out.println("========아이디가 존재합니다 ==========");
            return false;
        } else {
            // 권한 부여
            request.setRole("USER");
            // 회원탈퇴 여부
            request.setDeleteYn("N");

            request.encodingPassword(passwordEncoder);

            authMapper.updateMember(request);

            return true;
        }

    }


    /**
     * 회원 탈퇴
     *
     * @param id
     */
    @Transactional
    public boolean deleteMember(String id) {

        return authMapper.deleteMember(id);
    }

    /**
     * 회원 수 카운팅 (ID 중복 체크)
     * @param
     * @return 회원 수
     */
    public int countMemberByLoginId(final String loginId) {
        return authMapper.countByLoginId(loginId);
    }


    /**
     * 회원 리스트
     * @return
     */
    public List<MemberResponse> getMemberList() {

        List<MemberResponse> list = authMapper.getMemberList();

        return list;
    }

    /**
     * 회원 상세정보 조회
     * @return
     */
    public MemberResponse getMemberInfo(String id) {

        System.out.println("==============service" +  authMapper.findById(id));
        return authMapper.findById(id);
    }

    /**
     * 회원 상세정보 조회
     * @return
     */
    public MemberResponse getMemberSeq(String seq) {

        System.out.println("==============service" +  authMapper.findBySeq(seq));
        return authMapper.findBySeq(seq);
    }



}
