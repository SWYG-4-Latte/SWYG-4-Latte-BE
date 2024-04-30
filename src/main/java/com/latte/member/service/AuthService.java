package com.latte.member.service;

import com.latte.member.mapper.AuthMapper;
import com.latte.member.mapper.MemberMapper;
import com.latte.member.request.MemberRequest;
import com.latte.member.response.Gender;
import com.latte.member.response.MemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {



    @Autowired
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthMapper authMapper, PasswordEncoder passwordEncoder) {
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
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
