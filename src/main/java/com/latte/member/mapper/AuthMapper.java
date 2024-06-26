package com.latte.member.mapper;

import com.latte.member.request.MemberRequest;
import com.latte.member.request.PwChangeRequest;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuthMapper {



    // 회원정보 수정
    boolean updateMember(MemberRequest request);

    // 회원 비밀번호 수정
    boolean updatePassword(PwChangeRequest request);

    //public MemberResponse getMemberInfo(String Id);

    // 회원번호로 회원정보 찾기
    MemberResponse findBySeq(int seq);

    // 회원아이디로 회원정보 찾기
    MemberResponse findById(String id);

    // 회원아이디 찾기
    FindIdResponse findIdByNameEmail(@Param("email") String email);


    // 전체 회원 목록
    List<MemberResponse> getMemberList();

    // 회원 등록
    boolean insertMember(MemberRequest request);

    int countByIdEmail(@Param("id") String id, @Param("email") String email);

    // 아이디 중복 여부
    int countByLoginId(String id);

    // 닉네임 중복 여부
    int countByNickname(String nickname);

    // 사용자 이메일 존재 여부
    int countByEmail(String email);

    // 회원 탈퇴
    boolean deleteMember(int id);


    // 코드 일치 확인
    int verifyCode(@Param("email") String email, @Param("code") String code);

    boolean insertCode(@Param("email") String email, @Param("code") String code, @Param("codeDate") LocalDateTime codeDate);

    boolean deleteCode(@Param("email") String email);



}