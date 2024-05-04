package com.latte.member.mapper;

import com.latte.member.request.MemberRequest;
import com.latte.member.response.FindIdResponse;
import com.latte.member.response.MemberResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthMapper {



    void updateMember(MemberRequest request);

    //public MemberResponse getMemberInfo(String Id);

    MemberResponse findBySeq(int seq);

    List<MemberResponse> getMemberList();

    boolean insertMember(MemberRequest request);

    int countByLoginId(String id);


    int countByNickname(String nickname);


    boolean deleteMember(int id);

    MemberResponse findById(String id);

    FindIdResponse findIdByNameEmail(String name, String email);
}
