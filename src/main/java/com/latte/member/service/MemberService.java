package com.latte.member.service;

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
public class MemberService {


    @Autowired
    private final MemberMapper memberMapper;


    public MemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }


    /*
/**
     * 회원 수 카운팅 (ID 중복 체크)
     * @param
     * @return 회원 수
     *//*

    public int countMemberByLoginId(final String loginId) {
        return memberMapper.countByLoginId(loginId);
    }


    */
/**
     * 회원 리스트
     * @return
     *//*

    public List<MemberResponse> getMemberList() {

        List<MemberResponse> list = memberMapper.getMemberList();

        return list;
    }

    */
/**
     * 회원 상세정보 조회
     * @return
     */

    public MemberResponse getMemberInfo(String id) {

        return memberMapper.findById(id);
    }



}
